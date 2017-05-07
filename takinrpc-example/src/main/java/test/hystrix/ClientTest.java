package test.hystrix;

import java.util.concurrent.Future;

import org.apache.log4j.PropertyConfigurator;

public class ClientTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final HelloCommand command = new HelloCommand();
            //            System.out.println("result: " + command.execute());
            //            System.out.println("");

            Future<String> future = command.queue();
            System.out.println("result: " + future.get());
            System.out.println("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
