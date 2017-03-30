package test.simple;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;

import com.takin.rpc.client.ProxyFactory;

public class ClientTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("D:/log4j.properties");

            Hello hello = ProxyFactory.create(Hello.class, "test", "", null);

            for (int i = 0; i < 1; i++) {
                System.out.println(hello.say("xiaoming"));
                TimeUnit.SECONDS.sleep(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
