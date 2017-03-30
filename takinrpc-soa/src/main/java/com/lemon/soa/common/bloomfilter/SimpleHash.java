package com.lemon.soa.common.bloomfilter;
//package cn.ms.neural.common.bloomfilter;
//
///**
// * Simple Hash
// * 
// * me
// */
//public class SimpleHash {
//
//    /**
//     * cap为DEFAULT_SIZE，即用于结果的最大字符串的值 seed为计算hash值的一个key值，具体对应上文中的seeds数组
//     */
//    private int cap;
//    private int seed;
//
//    public SimpleHash(int cap, int seed) {
//        this.cap = cap;
//        this.seed = seed;
//    }
//
//    /**
//     * 计算hash的函数，用户可以选择其他更好的hash函数
//     * 
//     * @param value
//     * @return
//     */
//    public int hash(String value) {
//        int result = 0;
//        int length = value.length();
//        for (int i = 0; i < length; i++) {
//            result = seed * result + value.charAt(i);
//        }
//
//        return (cap - 1) & result;
//    }
//}
