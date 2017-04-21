package test.bench;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.PropertyConfigurator;

import com.google.common.base.Stopwatch;
import com.takin.rpc.client.ProxyFactory;

import test.Hello;

public class ClientTest {

    public static void main(String[] args) {
        try {
            int size = 20;
            PropertyConfigurator.configure("conf/log4j.properties");
            final Hello hello = ProxyFactory.create(Hello.class, "test", null, null);
            ExecutorService executor = Executors.newFixedThreadPool(50);
            for (int i = 0; i < size; i++) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
            hello.say("xiaoming");
            Thread.sleep(1000);
            System.out.println("start...");
            final CountDownLatch count = new CountDownLatch(size);

            Stopwatch watch = Stopwatch.createStarted();
            for (int i = 0; i < size; i++) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("result: " + hello.say("xiaoming"));
                        count.countDown();
                    }
                });
            }
            count.await();
            System.out.println(watch.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
