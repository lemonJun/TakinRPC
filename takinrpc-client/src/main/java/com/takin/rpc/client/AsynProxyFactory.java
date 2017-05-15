package com.takin.rpc.client;

import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.rpc.client.loadbalance.LoadBalance;

public class AsynProxyFactory {

    private static final Logger logger = LoggerFactory.getLogger(AsynProxyFactory.class);

    private final static ConcurrentHashMap<String, AsynProxyFactory> cache = new ConcurrentHashMap<String, AsynProxyFactory>(); //同步map

    private AsynProxyFactory() {
    }
    
    /**
     * Factory for Proxy - creation.
     * @param type the class of type
     * @param strUrl request URL
     * @return
     * @throws MalformedURLException
     */
    @SuppressWarnings("unchecked")
    public static AsynProxyFactory create(Class<?> interfaceclass, String serviceName) {//<T> T返回任意类型的数据？  返回代理的实例  泛型
        String key = String.format("%s_%s", interfaceclass.getName(), serviceName);
        AsynProxyFactory proxy = null;
        if (cache.containsKey(key)) {
            proxy = cache.get(key);
        }
        if (proxy == null) {
            proxy = createStandardProxy(interfaceclass, serviceName, null, null);
            if (proxy != null) {
                cache.put(key, proxy);
            }
        }
        return proxy;
    }

    @SuppressWarnings("unchecked")
    public static AsynProxyFactory create(Class<?> interfaceclass, String serviceName, Class<?> implMethod, LoadBalance balance) {//<T> T返回任意类型的数据？  返回代理的实例  泛型
        String key = String.format("%s_%s", interfaceclass.getName(), serviceName);
        AsynProxyFactory proxy = null;
        if (cache.containsKey(key)) {
            proxy = cache.get(key);
        }
        if (proxy == null) {
            proxy = createStandardProxy(interfaceclass, serviceName, implMethod, balance);
            if (proxy != null) {
                cache.put(key, proxy);
            }
        }
        return proxy;
    }

    /**
     * 
     * @param strUrl  连接字符串
     * @param interfaceClass  接口类
     * @return
     */
    private static AsynProxyFactory createStandardProxy(Class<?> interfaceclass, String serviceName, Class<?> implMethod, LoadBalance balance) {
        AsynProxyFactory facotry = new AsynProxyFactory();
        return facotry;
    }
    
    
    
    
    

}
