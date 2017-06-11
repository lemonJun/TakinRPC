package test.asyn;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.PropertyConfigurator;

import com.google.common.util.concurrent.RateLimiter;
import com.takin.rpc.client.ProxyFactory;
import com.takin.rpc.remoting.InvokeCallback;

public class ClientTest {

    private static final RateLimiter limit = RateLimiter.create(5000.0);
    private static AtomicInteger total = new AtomicInteger();
    private static AtomicInteger succ = new AtomicInteger();
    private static AtomicInteger fail = new AtomicInteger();

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final HelloAsyn hello = ProxyFactory.create(HelloAsyn.class, "test", null, null);
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

            while (true) {
                try {
                    if (limit.tryAcquire()) {
                        int triger = total.getAndIncrement();
                        hello.say("time:" + triger, new HelloCallBack());
                    }
                } catch (Exception e) {
                    fail.getAndIncrement();
                } catch (Throwable e) {
                    fail.getAndIncrement();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class HelloCallBack implements InvokeCallback {
        @Override
        public void operationComplete(Object obj) {
            succ.getAndIncrement();
            //            System.out.println((String) obj);
        }
    }
}
