package test.hystrix;

import java.util.concurrent.Future;

import org.apache.log4j.PropertyConfigurator;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import rx.Observable;

public class CommandHelloWorld extends HystrixCommand<String> {

    private final String name;

    public CommandHelloWorld(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
    }

    @Override
    protected String run() {
        return "Hello " + name + "!";
    }

    public static void main(String[] args) {
        PropertyConfigurator.configure("D:/log4j.properties");
        String s1 = new CommandHelloWorld("Bob").execute();
        Future<String> s2 = new CommandHelloWorld("Bob").queue();
        Observable<String> s3 = new CommandHelloWorld("Bob").observe();
        System.out.println(s1);
        //        System.out.println(s2); 
        //        System.out.println(s3);
    }
}
