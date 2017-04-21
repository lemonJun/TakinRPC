package test;

import java.util.concurrent.CountDownLatch;

import com.google.common.base.Stopwatch;

public class TestCountDownLatch {

    public static void main(String[] args) {
        Stopwatch watch = Stopwatch.createStarted();
        CountDownLatch latch = new CountDownLatch(10);
        System.out.println(watch.toString());
        for (int i = 0; i < 10; i++) {
            latch.countDown();
            System.out.println(watch.toString());
        }
    }
    
    

    
    
}
