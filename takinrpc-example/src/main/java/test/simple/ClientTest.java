package test.simple;

import org.apache.log4j.PropertyConfigurator;

import com.google.common.base.Stopwatch;
import com.takin.rpc.client.ProxyFactory;

public class ClientTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            Hello hello = ProxyFactory.create(Hello.class, "test", null, null);
            hello.say("xiaoming");
            Thread.sleep(1000);
            System.out.println("start...");
            Stopwatch watch = Stopwatch.createStarted();
            for (int i = 0; i < 10; i++) {
                System.out.println("result: " + hello.say("xiaoming" + i));
            }
            System.out.println(watch.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
