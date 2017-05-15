package test.bench;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.PropertyConfigurator;

import com.google.common.util.concurrent.RateLimiter;
import com.takin.rpc.client.ProxyFactory;

import test.Hello;

public class SyncBenchTestExecutor {

    private static final RateLimiter limit = RateLimiter.create(5000.0);

    public static void main(String[] args) {
        try {
            AtomicInteger total = new AtomicInteger();
            AtomicInteger succ = new AtomicInteger();
            AtomicInteger fail = new AtomicInteger();
            PropertyConfigurator.configure("conf/log4j.properties");
            final Hello hello = ProxyFactory.create(Hello.class, "test", null, null);
            hello.say("a");
            hello.say("a");
            for (int i = 2; i >= 0; i--) {
                Thread.sleep(1000);
                System.out.println(i + " ...");
            }

            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    System.out.println(String.format("total:%d succ:%s fail:%s", total.get(), succ.get(), fail.get()));
                }
            }, 1000, 1000, TimeUnit.MILLISECONDS);

            Executors.newFixedThreadPool(3).submit(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        try {
                            if (limit.tryAcquire()) {
                                int triger = total.getAndIncrement();
                                hello.say("time:" + triger);
                                succ.getAndIncrement();
                            }
                        } catch (Exception e) {
                            fail.getAndIncrement();
                        } catch (Throwable e) {
                            fail.getAndIncrement();
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
    }
}
