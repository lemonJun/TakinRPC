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
            System.out.println("result: " + hello.say("aa"));
            System.out.println("");

            //            System.out.println("result: " + hello.say("bb"));
            //            System.out.println("");
            //
            //            System.out.println("result: " + hello.say("dd"));
            //            System.out.println("");
            //
            //            System.out.println("result: " + hello.say("ee"));
            //            System.out.println("");
            //
            //            System.out.println("result: " + hello.say("ff"));
            //            System.out.println("");
            //
            //            System.out.println("result: " + hello.say("hh"));
            //            System.out.println("");

            System.out.println("result: " + hello.say("u"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
