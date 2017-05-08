package test.tcc;

import org.apache.log4j.PropertyConfigurator;

import com.takin.rpc.client.ProxyFactory;

public class ClientTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final AcountService hello = ProxyFactory.create(AcountService.class, "test", null, null);
            System.out.println("result: " + hello.deal(11L));
            System.out.println("");
            //            System.out.println("result: " + hello.deal(222L));
            //            System.out.println("");

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
