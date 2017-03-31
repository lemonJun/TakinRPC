package test.simple;

import org.apache.log4j.PropertyConfigurator;

import com.takin.rpc.client.ProxyFactory;

import test.Hello;

public class ClientTest {

    public static void main(String[] args) {
        try {
            int size = 10;
            PropertyConfigurator.configure("conf/log4j.properties");
            final Hello hello = ProxyFactory.create(Hello.class, "test", null, null);
            hello.say("xiaoming");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
