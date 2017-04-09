package com.raft;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2016/10/31.
 */
public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("shit");
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("shit1");
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 1000);
        timer.schedule(task2, 1000);

        /*
        final BasicFuture<String> future = new BasicFuture<String>(new FutureCallback<String>() {
            public void completed(String s) {
                System.out.println("completed");
            }
        
            public void failed(Throwable th) {
                System.out.println("failed");
            }
        
            public void cancelled() {
                System.out.println("cancelled");
            }
        });
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                future.completed("shit");
            }
        });
        thread.start();
        
        try {
            System.out.println(future.get(6000, TimeUnit.MILLISECONDS));
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        */
    }
}
