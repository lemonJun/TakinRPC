# TakinRPC
RPC框架，基于netty，实现了远程调用、服务治理、TCC事务等功能


## 测试
服务端
```
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

客户端
```
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
