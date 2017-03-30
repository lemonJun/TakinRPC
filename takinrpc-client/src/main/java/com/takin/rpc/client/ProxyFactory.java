package com.takin.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ProxyFactory
 * @author Service Platform Architecture Team (spat@58.com)
 * 
 * 
 */
public class ProxyFactory {

    private final static ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<String, Object>(); //同步map

    private static volatile ProxyFactory instance;

    private ProxyFactory() {
    }

    /**
     * Factory for Proxy - creation.
     * @param type the class of type
     * @param strUrl request URL
     * @return
     * @throws MalformedURLException
     */
    //url = "tcp://demo/NewsService";
    public static <T> T create(Class<?> type, String strUrl) {//<T> T返回任意类型的数据？  返回代理的实例  泛型
        String key = strUrl.toLowerCase();
        Object proxy = null;
        if (cache.containsKey(key)) {
            proxy = cache.get(key);
        }
        if (proxy == null) {
            proxy = createStandardProxy(strUrl, type);
            if (proxy != null) {
                cache.put(key, proxy);
            }
        }
        return (T) proxy;
    }

    /**
     * 
     * @param strUrl  连接字符串
     * @param interfaceClass  接口类
     * @return
     */
    //url = "tcp://demo/NewService";
    private static Object createStandardProxy(String strUrl, Class<?> interfaceClass) {
        String serviceName = "";
        String lookup = "";//接口实现类
        strUrl = strUrl.replace("tcp://", "");
        String[] splits = strUrl.split("/");
        if (splits.length == 2) {
            serviceName = splits[0]; //=demo
            lookup = splits[1]; //=NewService
        }
        InvocationHandler handler = new ProxyStandard(interfaceClass, serviceName, lookup);
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { interfaceClass }, handler);
    }

}