package zk;

import org.apache.log4j.PropertyConfigurator;

import com.github.zkclient.ZkClient;

public class ZKClientTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            ZkClient client = new ZkClient("localhost:2181");
            client.createEphemeral("/rpc", "home".getBytes());
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
