package zk;

import org.apache.log4j.PropertyConfigurator;

import com.github.zkclient.ZkServer;

public class ZKServerTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            ZkServer zk = new ZkServer("D:/zk/data", "D:/zk/log", 2181);
            zk.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
