package test.bench;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.PropertyConfigurator;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.RateLimiter;
import com.takin.rpc.client.ProxyFactory;

import test.Hello;

public class ASyncBenchTest {

    private static final RateLimiter limit = RateLimiter.create(5000.0);

    private static ExecutorService sendexecutor = Executors.newFixedThreadPool(1);
    private static ListeningExecutorService listen = MoreExecutors.listeningDecorator(sendexecutor);

    public static void main(String[] args) {
        try {
            AtomicInteger total = new AtomicInteger();
            AtomicInteger succ = new AtomicInteger();
            AtomicInteger fail = new AtomicInteger();
            PropertyConfigurator.configure("conf/log4j.properties");
            final Hello hello = ProxyFactory.create(Hello.class, "test", null, null);
            hello.say("a");
            hello.say("a");
            for (int i = 5; i >= 0; i--) {
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
                        ListenableFuture<String> future = listen.submit(new Callable<String>() {
                            @Override
                            public String call() throws Exception {
                                int triger = total.getAndIncrement();
                                return hello.say("time:" + triger);
                            }
                        });
                        //                        Futures.addCallback(future, new FutureCallback<String>() {
                        //                            @Override
                        //                            public void onSuccess(String result) {
                        //                                succ.getAndIncrement();
                        //                            }
                        //
                        //                            @Override
                        //                            public void onFailure(Throwable t) {
                        //                                fail.getAndIncrement();
                        //                            }
                        //                        });
                    }
                } catch (Exception e) {
                    fail.getAndIncrement();
                } catch (Throwable e) {
                    fail.getAndIncrement();
                }
            }
        } catch (Exception e) {
        }
    }
}
