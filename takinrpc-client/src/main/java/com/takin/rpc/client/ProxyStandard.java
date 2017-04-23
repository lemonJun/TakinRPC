package com.takin.rpc.client;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.reflect.AbstractInvocationHandler;
import com.takin.rpc.client.loadbalance.ConsistentHashLoadBalance;
import com.takin.rpc.client.loadbalance.LoadBalance;
import com.takin.rpc.remoting.netty5.RemotingProtocol;

/**
 * ProxyStandard
 *
 * @author Service Platform Architecture Team (spat@58.com)
 */
public class ProxyStandard extends AbstractInvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProxyStandard.class);

    private Class<?> defineClass;
    private Class<?> implClass = null;
    private String serviceName = "";
    private LoadBalance balance = new ConsistentHashLoadBalance();
    @SuppressWarnings("unused")
    private boolean asyn = false;

    private final ExecutorService executor;

    /** 
     * 
     * @param interfaceClass 接口类 
     * @param serviceName 服务名
     * @param lookup 接口实现类
     * @param balance 使用哪一个负载均衡算法
     */
    public ProxyStandard(Class<?> defineClass, String serviceName, Class<?> implClass, LoadBalance balance) {
        this.defineClass = defineClass;
        this.serviceName = serviceName;
        this.implClass = implClass;
        if (balance != null) {
            this.balance = balance;
        }
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

    public ProxyStandard(Class<?> defineClass, String serviceName, Class<?> implClass, LoadBalance balance, boolean asyn) {
        this.defineClass = defineClass;
        this.serviceName = serviceName;
        this.implClass = implClass;
        if (balance != null) {
            this.balance = balance;
        }
        this.asyn = asyn;
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        String address = "";
        try {
            Type[] typeAry = method.getGenericParameterTypes();//ex:java.util.Map<java.lang.String, java.lang.String>
            Class<?>[] clsAry = method.getParameterTypes();//ex:java.util.Map
            if (args == null) {
                args = new Object[0];
            }
            if (args.length != typeAry.length) {
                throw new Exception("argument count error!");
            }

            RemotingProtocol<?> message = new RemotingProtocol<>();
            message.setDefineClass(defineClass);
            message.setImplClass(implClass);
            message.setMethod(method.getName());
            message.setArgs(args);
            message.setmParamsTypes(clsAry);
            message.setmReturnType(method.getReturnType());
            address = balance.select(NamingFactory.getInstance().getConfigAddr(serviceName), "");
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("request: %s", JSON.toJSONString(message)));
            }

            //            message = RemotingNettyClient.getInstance().invokeSync(address, message, 2000);
            Future<RemotingProtocol> fu = executor.submit(new InvokeThread(address, message));
            RemotingProtocol result = fu.get();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("response: %s", JSON.toJSONString(message)));
            }
            return result.getResultJson();
        } catch (Exception e) {
            logger.error("invoke error", e);
        }
        return null;
    }
}

@SuppressWarnings("rawtypes")
class InvokeThread implements Callable<RemotingProtocol> {

    private String address;
    private RemotingProtocol message;

    public InvokeThread(final String address, final RemotingProtocol message) {
        this.address = address;
        this.message = message;
    }

    @Override
    public RemotingProtocol call() throws Exception {
        RemotingProtocol msg = RemotingNettyClient.getInstance().invokeSync(address, message, 3000);
        return msg;
    }

}
