package com.takin.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.Reflection;
import com.takin.rpc.client.loadbalance.LoadBalance;

import net.sf.cglib.proxy.Enhancer;

/**
 * ProxyFactory
 * @author Service Platform Architecture Team (spat@58.com)
 * 
 * 
 */
public class ProxyFactory {

    private static final Logger logger = LoggerFactory.getLogger(ProxyFactory.class);

    private final static ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<String, Object>(); //同步map

    private ProxyFactory() {
    }

    /**
     * Factory for Proxy - creation.
     * @param type the class of type
     * @param strUrl request URL
     * @return
     * @throws MalformedURLException
     */
    public static <T> T create(Class<?> interfaceclass, String serviceName) {//<T> T返回任意类型的数据？  返回代理的实例  泛型
        return create(interfaceclass, serviceName, null, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<?> interfaceclass, String serviceName, Class<?> implMethod, LoadBalance balance) {//<T> T返回任意类型的数据？  返回代理的实例  泛型
        String key = String.format("%s_%s", interfaceclass.getName(), serviceName);
        Object proxy = null;
        if (cache.containsKey(key)) {
            proxy = cache.get(key);
        }
        if (proxy == null) {
            proxy = createStandardProxy(interfaceclass, serviceName, implMethod, balance);
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
    private static Object createStandardProxy(Class<?> interfaceclass, String serviceName, Class<?> implMethod, LoadBalance balance) {
        InvocationHandler handler = new ProxyStandard(interfaceclass, serviceName, implMethod, balance);
        //        Object obj = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { interfaceclass }, handler);
        Object obj = Reflection.newProxy(interfaceclass, handler);
        logger.info(String.format("create jdkproxy for %s ", interfaceclass.getName()));
        return obj;
    }

    private static final Enhancer enhancer = new Enhancer();

    /**
     * cglib实现的代理
     * @param interfaceclass
     * @param serviceName
     * @param implMethod
     * @param balance
     * @return
     */
    private static Object createCGlibProxy(Class<?> interfaceclass, String serviceName, Class<?> implMethod, LoadBalance balance) {
        //设置需要创建子类的类  
        enhancer.setSuperclass(interfaceclass);
        CGlibProxy proxy = new CGlibProxy(interfaceclass, serviceName, implMethod, balance);
        enhancer.setCallback(proxy);
        //通过字节码技术动态创建子类实例  
        logger.info(String.format("create cglibproxy for %s ", interfaceclass.getName()));
        return enhancer.create();
    }

}
