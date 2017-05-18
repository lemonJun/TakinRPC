package test.simple;

import java.util.Date;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.takin.rpc.client.ProxyFactory;

import test.Hello;
import test.User;

public class ClientBeanTest {

    @Test
    public void testbean() {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final Hello hello = ProxyFactory.create(Hello.class, "test", null, null);

            User u = new User();
            u.setAge(12);
            u.setName("nana");
            u.setStart(new Date());
            System.out.println("");
            u.setName("lua");
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testList() {
        try {
            PropertyConfigurator.configure("conf/log4j.properties");
            final Hello hello = ProxyFactory.create(Hello.class, "test", null, null);

            User u = new User();
            u.setAge(12);
            u.setName("nana");
            u.setStart(new Date());
            System.out.println("result: " + hello.getall(u));
            System.out.println("");
            u.setName("lua");
            System.out.println("result: " + hello.getall(u));
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
