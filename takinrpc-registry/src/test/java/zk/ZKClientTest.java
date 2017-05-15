package zk;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.takin.rpc.zkclient.ZkClient;

public class ZKClientTest {

    ZkClient client = null;

    public void init() {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            client = new ZkClient("localhost:2182");
            //            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void add() {
        try {
            init();
            client.createPersistent("/te", "1".getBytes());
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
