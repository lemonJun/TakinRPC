//package com.raft;
//
//import com.google.common.util.concurrent.RateLimiter;
//
///**
// * Created by Administrator on 2016/11/2.
// */
//public class TestRateLimiter {
//    public static void main(String[] args) {
//        Long start = System.currentTimeMillis();
//        RateLimiter limiter = RateLimiter.create(10.0); // 每秒不超过10个任务被提交
//        for (int i = 0; i < 100; i++) {
//            limiter.acquire(); // 请求RateLimiter, 超过permits会被阻塞
//            System.out.println("call execute.." + i);
//
//        }
//        Long end = System.currentTimeMillis();
//
//        System.out.println(end - start);
//    }
//}
