package com.takin.rpc.server;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(ServerBootstrap.class);

    public static void main(String[] args) {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
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
            String path = Path.getCurrentPath() + File.separator + "conf" + File.separator;
            PropertyConfigurator.configure(path + "/log4j.properties");
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
