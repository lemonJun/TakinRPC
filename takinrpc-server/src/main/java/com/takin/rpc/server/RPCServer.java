package com.takin.rpc.server;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCServer {

    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);

    public static void main(String[] args) {
        RPCServer rpc = new RPCServer();
        rpc.init(args, true);
    }

    private final RPCContext context = new RPCContext();

    public void init(String[] args, boolean online) {
        try {
            if (!online) {
                String userDir = System.getProperty("user.dir");
                runEclipse(userDir);
            } else {
                String appName = null;
                for (int i = 0; i < args.length; i++) {
                    if (args[i].startsWith("-D")) {
                        String[] aryArg = args[i].split("=");
                        if (aryArg.length == 2) {
                            if (aryArg[0].equalsIgnoreCase("-rpc.name")) {
                                appName = aryArg[1];
                            }
                        }
                    }
                }
                String userDir = System.getProperty("app.dir");
                runOnline(appName, userDir);
            }
            initConf();
            initDI();
            initService();
            GuiceDI.getInstance(RemotingNettyServer.class).start();
            logger.info("takin rpc server start up succ");
        } catch (Exception e) {
            logger.info("takin rpc server start up fail", e);
        }
    }

    private void initService() throws Exception {
        GuiceDI.getInstance(DynamicClassLoader.class).addFolder(context.getServicePath());

    }

    private void runEclipse(String rootPath) throws Exception {
        String appFolder = rootPath;
        String appConfigFolder = appFolder + File.separator + "conf";
        String serviceFolder = appFolder + File.separator + "target";
        context.setConfigPath(appConfigFolder);
        context.setServicePath(serviceFolder);
    }

    private void runOnline(String rpcName, String rootPath) throws Exception {
        String appFolder = rootPath + File.separator + "app" + File.separator + rpcName;
        String appConfigFolder = appFolder + File.separator + "conf";
        //        String appConfigDefaultPath = appConfigFolder + File.separator + "app_config.xml";
        //        String log4jFilePath = rootPath + File.separator + "log" + File.separator + rpcName;
        //        String appClassLib = appFolder + File.separator + "lib";
        context.setConfigPath(appConfigFolder);
        context.setServicePath(appFolder);
        context.setRpcName(rpcName);
    }

    private void initConf() {
        try {
            String logpath = context.getConfigPath() + File.separator + "log4j.properties";
            PropertyConfigurator.configure(logpath);
            logger.info(String.format("log4j path:%s", logpath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDI() {
        try {
            GuiceDI.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
