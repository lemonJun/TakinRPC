package test.hystrix;

import java.util.concurrent.Future;

import org.apache.log4j.PropertyConfigurator;

import com.google.common.util.concurrent.RateLimiter;

import rx.Observable;

public class ClientTest {

    public static void main(String[] args) {
        try {
            RateLimiter limit = RateLimiter.create(100d);
            PropertyConfigurator.configure("conf/log4j.properties");
            while (true) {
                if (limit.tryAcquire()) {
                    final HelloCommand command = new HelloCommand();
                    //            System.out.println("result: " + command.execute());
                    //            System.out.println("");

                    Future<String> future = command.queue();
                    System.out.println("result: " + future.get());
                    System.out.println("");
                }
            }

            //            Observable<String> observe = command.observe();
            //            observe.asObservable().subscribe((result) -> {
            //                System.out.println(result);
            //            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
