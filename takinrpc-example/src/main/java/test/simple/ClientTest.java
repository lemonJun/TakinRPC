package test.simple;

import org.apache.log4j.PropertyConfigurator;

import com.takin.rpc.client.ProxyFactory;

import test.Hello;

public class ClientTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final Hello hello = ProxyFactory.create(Hello.class, "test", null, null);
            System.out.println("result: " + hello.say("xiaoming"));
            System.out.println("");
            System.out.println("result: " + hello.hi(2));
            System.out.println("");
            //            System.out.println("result: " + hello.hi(2));
            //            System.out.println("");
            //            System.out.println("result: " + hello.hi(2));
            //            System.out.println("");
            //            System.out.println("result: " + hello.hi(2));
            System.out.println("");
            // 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
