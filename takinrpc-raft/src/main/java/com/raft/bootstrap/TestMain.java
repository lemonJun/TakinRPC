package com.raft.bootstrap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.RateLimiter;

/**
 * Created by ywjay on 17/2/27.
 */
public class TestMain {

    public static void main(String[] args) {
        RateLimiter limiter = RateLimiter.create(100.0);

        ExecutorService exector = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            limiter.acquire();
            System.out.println("shit");
        }

        //            exector.execute(new TestRun(limiter));
        exector.shutdown();

    }

    static class TestRun implements Runnable {
        private RateLimiter limiter;

        public TestRun(RateLimiter limiter) {
            this.limiter = limiter;
        }

        public void run() {
            //            limiter.acquire();
            System.out.println("get lock");
            try {
                //                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
