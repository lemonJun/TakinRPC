package com.takin.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.takin.emmet.reflect.GenericsUtils;
import com.takin.rpc.remoting.netty5.RemotingProtocol;

/**
 * 在原实现中，是直接调用的remoting的方法，没有使用代理
 * 
 * scf是实现的invocationhandler,然后通过这个进行调用
 * 
 * 
 * @author lemon
 * @version 1.0
 * @date  2015年10月13日 下午2:51:21
 * @see 
 * @since
 */
public class JDKProxy {

    private static final Logger logger = LoggerFactory.getLogger(JDKProxy.class);
    //代理缓存
    public static final ConcurrentHashMap<String, Object> proxyMap = GenericsUtils.newConcurrentHashMap();
    //    public static final ConcurrentHashMap<String, Future<Object>> futureyMap = GenericsUtils.newConcurrentHashMap();
    public static final ExecutorService executor = Executors.newFixedThreadPool(6);

    private final RemotingNettyClient remotingclient;

    public JDKProxy(RemotingNettyClient remotingclient) {
        this.remotingclient = remotingclient;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> T createProxy(final Class clazz) {
        logger.info(String.format("Create New JDK Proxy %s", ""));
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { clazz }, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RemotingProtocol message = new RemotingProtocol();
                //                message.setClazz(clazz.getName());
                message.setMethod(method.getName());
                message.setArgs(args);
                message.setmParamsTypes(method.getParameterTypes());
                String address = "127.0.0.1:6871";//应该从某个地方获取到
                logger.info(String.format("request: %s", JSONObject.toJSONString(message)));
                RemotingProtocol resultMessage = remotingclient.invokeSync(address, message, 2000);
                //                logger.info(String.format("response: %s", JSONObject.toJSONString(message)));
                System.out.println("return:" + resultMessage.getResultJson());
                return resultMessage.getResultJson();
            }
        });

        return (T) proxy;
    }
}
