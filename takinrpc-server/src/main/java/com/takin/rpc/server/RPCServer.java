package com.takin.rpc.server;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service.Listener;
import com.google.common.util.concurrent.Service.State;
import com.google.inject.Singleton;
import com.takin.emmet.file.PropertiesHelper;
import com.takin.rpc.server.registry.ServerRegistry;

/**
 * rpc服务主入口类
 *
 * @author WangYazhou
 * @date  2017年5月9日 上午11:51:48
 * @see 
 */
public class RPCServer {

    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);

    public static void main(String[] args) {
        try {
            RPCServer rpc = new RPCServer();
            rpc.init(args, true);
            rpc.start();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("", e);
            System.exit(-1);
        } finally {

        }
    }

    private final RPCContext context = new RPCContext();

    public void init(String[] args, boolean online) throws Exception {
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
            initDI();
            initConf();
            initService();
            logger.info("takin rpc server start up succ");
        } catch (Exception e) {
            logger.info("takin rpc server start up fail", e);
        }
    }

    /**
     * 启动过滤器
     * 启动注册服务
     * 启动服务 
     * 
     * @throws Exception
     */
    public void start() throws Exception {
        GuiceDI.getInstance(ServerRegistry.class).startAsync().addListener(new Listener() {
            @Override
            public void running() {
                logger.info("zk registry running");
            }
        }, MoreExecutors.directExecutor());
        GuiceDI.getInstance(RemotingNettyServer.class).startAsync().awaitRunning();
    }

    public void shutdown() {
        GuiceDI.getInstance(ServerRegistry.class).stopAsync();
        GuiceDI.getInstance(RemotingNettyServer.class).stopAsync().addListener(new Listener() {

            @Override
            public void terminated(State from) {
                super.terminated(from);
                logger.info("zk registry stopped");
            }
        }, MoreExecutors.directExecutor());
    }

    /**
    * 扫描:
    * 接口服务类
    * 初始化类
    * 过滤器类
    * @throws Exception
    */
    private void initService() throws Exception {
        try {
            final DynamicClassLoader classloader = GuiceDI.getInstance(DynamicClassLoader.class);
            classloader.addFolder(context.getServicePath(), context.getLibPath());
            GuiceDI.getInstance(Scaner.class).scanInfo(classloader);
            GuiceDI.getInstance(FilterChain.class).init();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private void runEclipse(String rootPath) throws Exception {
        String appFolder = rootPath;
        String appConfigFolder = appFolder + File.separator + "conf";
        String serviceFolder = appFolder + File.separator + "target";
        context.setConfigPath(appConfigFolder);
        context.setServicePath(serviceFolder);
        context.setLibPath(serviceFolder + File.separator + "lib");
    }

    private void runOnline(String rpcName, String rootPath) throws Exception {
        String appFolder = rootPath + File.separator + "app" + File.separator + rpcName;
        String appConfigFolder = appFolder + File.separator + "conf";
        context.setConfigPath(appConfigFolder);
        context.setServicePath(appFolder);
        context.setRpcName(rpcName);
        context.setLibPath(appFolder + File.separator + "lib");
    }

    private void initConf() {
        try {
            String logpath = context.getConfigPath() + File.separator + "log4j.properties";
            PropertyConfigurator.configure(logpath);
            logger.info(String.format("log4j path:%s", logpath));

            String serverpath = context.getConfigPath() + File.separator + "server.properties";
            NettyServerConfig config = GuiceDI.getInstance(NettyServerConfig.class);
            PropertiesHelper pro = new PropertiesHelper(serverpath);
            config.setSelectorThreads(pro.getInt("selectorThreads"));
            config.setWorkerThreads(pro.getInt("workerThreads"));
            config.setListenPort(pro.getInt("server.Port"));
            config.setUsezk(pro.getBoolean("use.zk"));
            config.setZkhosts(pro.getString("zk.hosts"));

            logger.info(JSON.toJSONString(config));

        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public RPCContext getContext() {
        return context;
    }

    private void initDI() {
        try {
            GuiceDI.init();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
