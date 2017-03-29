package test.simple;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;

import com.takin.rpc.client.JDKProxy;
import com.takin.rpc.client.NettyClientConfig;
import com.takin.rpc.client.RemotingNettyClient;

public class ClientTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("D:/log4j.properties");

            NettyClientConfig config = new NettyClientConfig();
            RemotingNettyClient client = new RemotingNettyClient(config);
            client.start();
            JDKProxy proxy = new JDKProxy(client);
            Hello hello = proxy.createProxy(Hello.class);

            for (int i = 0; i < 1; i++) {
                System.out.println(hello.say("xiaoming"));
                TimeUnit.SECONDS.sleep(1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
