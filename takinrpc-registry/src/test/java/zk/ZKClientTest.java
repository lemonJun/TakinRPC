package zk;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.github.zkclient.ZkClient;

public class ZKClientTest {

    ZkClient client = null;

    public static void main(String[] args) {

    }

    public void init() {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            client = new ZkClient("localhost:2181");
            client.createEphemeral("/rpc", "home".getBytes());
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void add() {
        init();
        client.createPersistent("/rpc");
        client.createPersistent("host1", "1".getBytes());
    }

}
