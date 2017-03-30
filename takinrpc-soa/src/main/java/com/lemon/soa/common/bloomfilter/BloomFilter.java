package com.lemon.soa.common.bloomfilter;
//package cn.ms.neural.common.bloomfilter;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.BitSet;
//
//import cn.ms.neural.common.logger.ILogger;
//import cn.ms.neural.common.logger.LoggerManager;
//
///**
// * 布隆过滤器，传统的布隆过滤器不支持从集合中删除成员
// * 
// * me
// */
//public class BloomFilter {
//
//    private static final ILogger sysBootLog = LoggerManager.getSysBootLog();
//
//    /**
//     * DEFAULT_SIZE为2的29次方，即此处的左移28位
//     */
//    public static final int DEFAULT_SIZE = 2 << 28;
//    /**
//     * 不同哈希函数的种子，一般取质数 seeds数组共有8个值，则代表采用8种不同的哈希函数
//     */
//    private int[] seeds = new int[] { 3, 5, 7, 11, 13, 31, 37, 61 };
//    /**
//     * 初始化一个给定大小的位集 BitSet实际是由“二进制位”构成的一个Vector。 假如希望高效率地保存大量“开－关”信息，就应使用BitSet.
//     */
//    private BitSet bitSets = new BitSet(DEFAULT_SIZE);
//    /**
//     * 构建hash函数对象
//     */
//    private SimpleHash[] hashFuns = new SimpleHash[seeds.length];
//    /**
//     * 布隆过滤器配置文件存放路径
//     */
//    private String path = "";
//
//    public BloomFilter(String path) {
//        /**
//         * 给出所有的hash值，共计seeds.length个hash值。共8位。
//         * 通过调用SimpleHash.hash(),可以得到根据8种hash函数计算得出hash值。
//         * 传入DEFAULT_SIZE(最终字符串的长度），seeds[i](一个指定的质数)即可得到需要的那个hash值的位置。
//         */
//        for (int i = 0; i < seeds.length; i++) {
//            hashFuns[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
//        }
//        this.path = path;// 配置文件路径地址
//    }
//
//    /**
//     * 将给定的字符串标记到bitSets中，即设置字符串的8个函数值的位置为1
//     * 
//     * @param value
//     */
//    public synchronized void add(String value) {
//        for (SimpleHash hashFun : hashFuns) {
//            bitSets.set(hashFun.hash(value), true);
//        }
//    }
//
//    /**
//     * 判断给定的字符串是否已经存在在bloofilter中，如果存在返回true，不存在返回false
//     * 
//     * @param value
//     * @return
//     */
//    public synchronized boolean isExit(String value) {
//        if (null == value) {
//            return false;
//        }
//        for (SimpleHash hashFun : hashFuns) {
//            // 如果判断8个hash函数值中有一个位置不存在即可判断为不存在Bloofilter中
//            if (!bitSets.get(hashFun.hash(value))) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 读取配置文件
//     */
//    public void init() {
//        File file = new File(path);
//        FileInputStream in = null;
//        try {
//            in = new FileInputStream(file);
//            long lt = System.currentTimeMillis();
//            read(in);
//            if (sysBootLog.isInfoEnabled()) {
//                sysBootLog.info("The now total memory size:" + Runtime.getRuntime().totalMemory() + ", read all expend:" + (System.currentTimeMillis() - lt) + "ms");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 根据传入的流，初始化bloomfilter
//     * 
//     * @param in
//     */
//    private void read(InputStream in) {
//        if (null == in) { // 如果in为null，则返回
//            return;
//        }
//        int i = 0;
//        InputStreamReader reader = null;
//        try {
//            reader = new InputStreamReader(in, "UTF-8");
//            BufferedReader buffReader = new BufferedReader(reader, 512);
//            String theWord = null;
//            do {
//                i++;
//                theWord = buffReader.readLine();
//                if (theWord != null && !theWord.trim().equals("")) {
//                    add(theWord);
//                }
//                if (i % 10000 == 0) {
//                    if (sysBootLog.isInfoEnabled()) {
//                        sysBootLog.info("The now no is:" + i);
//                    }
//                }
//            } while (theWord != null);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (reader != null) {
//                    reader.close();
//                }
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//}
