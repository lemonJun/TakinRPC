# TakinRPC
1. 基于Netty实现远程通信；
2. 服务端使用Guice进行各组件管理和整合；
3. 基于kyro实现高效通用序列化协议；
4. 基于zookeeper实现服务注册；
5. 基于代理与反射使得接口简单易用；
6. 客户端实现随机、一致性哈希、roundbin三种负载均衡算法；
7. 客户端基于hystrix做限流和及时熔断；
8. 对每一次客户端请求使用Countdownlatch做限时保护 ；
9. 


## 同步测试

```
服务端

public class ServerTest {

    public static void main(String[] args) {
        try {
            RPCServer server = new RPCServer();
            server.init(new String[] {}, false);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```


```
     客户端
     
    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final Hello hello = ProxyFactory.create(Hello.class, "test", null, null);
            System.out.println("result: " + hello.say("xiaoming"));
            System.out.println("");
            System.out.println("result: " + hello.say("aa"));
            System.out.println("");

            System.out.println("result: " + hello.say("u"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

## 异步测试
```
异步客户端

public class ClientTest {

    private static final RateLimiter limit = RateLimiter.create(2000.0);
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


```
