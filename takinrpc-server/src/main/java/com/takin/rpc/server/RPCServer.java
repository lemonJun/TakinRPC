package com.takin.rpc.server;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCServer {

    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);

    public static void main(String[] args) {
        try {
            RPCServer bootstrap = new RPCServer();
            bootstrap.initConf();

            bootstrap.initDI();

            GuiceDI.getInstance(RemotingNettyServer.class).start();
            logger.info("takin rpc server start up succ");
        } catch (Exception e) {
            logger.info("takin rpc server start up fail", e);
        }
    }

    private void initConf() {
        try {
            String logpath = Path.getCurrentPath() + File.separator + "conf" + File.separator + "/log4j.properties";
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
