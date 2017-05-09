package test;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.takin.rpc.client.NamingFactory;

public class NamingTest {

    static {
        PropertyConfigurator.configure("D:/log4j.properties");
    }

    @Test
    public void init() {
        NamingFactory.getInstance();
    }

    public static void main(String[] args) {
        try {
            NamingFactory.getInstance();
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
