package com.takin.rpc.client;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.reflect.AbstractInvocationHandler;
import com.takin.emmet.util.AddressUtil;
import com.takin.rpc.client.loadbalance.ConsistentHashLoadBalance;
import com.takin.rpc.client.loadbalance.LoadBalance;
import com.takin.rpc.remoting.InvokeCallback;
import com.takin.rpc.remoting.netty4.RemotingProtocol;

/**
 * ProxyStandard
 *
 * @author Service Platform Architecture Team (spat@58.com)
 */
public class ProxyStandard extends AbstractInvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProxyStandard.class);

    private final AtomicLong sequence = new AtomicLong();

    private Class<?> defineClass;
    private Class<?> implClass = null;
    private String serviceName = "";
    private LoadBalance balance = new ConsistentHashLoadBalance();
    @SuppressWarnings("unused")
    private boolean asyn = false;
    private String localaddress = "";

    private final ThreadPoolExecutor executor;

    /** 
     * 
     * @param interfaceClass 接口类 
     * @param serviceName 服务名
     * @param lookup 接口实现类
     * @param balance 使用哪一个负载均衡算法
     */
    public ProxyStandard(Class<?> defineClass, String serviceName, Class<?> implClass, LoadBalance balance) {
        this(defineClass, serviceName, implClass, balance, false);
    }

    public ProxyStandard(Class<?> defineClass, String serviceName, Class<?> implClass, LoadBalance balance, boolean asyn) {
        this.defineClass = defineClass;
        this.serviceName = serviceName;
        this.implClass = implClass;
        if (balance != null) {
            this.balance = balance;
        }

        this.asyn = asyn;
        localaddress = AddressUtil.getLocalAddress();

        //        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //                logger.info(String.format("taskcount:%d completecount:%d", executor.getTaskCount(), executor.getCompletedTaskCount()));
            }
        }, 1000, 2000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        String address = "";
        try {
            InvokeCallback callback = null;
            boolean sync = false;
            //            Stopwatch watch = Stopwatch.createStarted();
            Type[] typeAry = method.getGenericParameterTypes();//ex:java.util.Map<java.lang.String, java.lang.String>
            Class<?>[] clsAry = method.getParameterTypes();//ex:java.util.Map
            if (args == null) {
                args = new Object[0];
            } else {
                for (int i = 0; i < args.length; i++) {
                    Class<?> clz = clsAry[i];
                    if (clz.isAssignableFrom(InvokeCallback.class)) {
                        sync = true;
                        callback = (InvokeCallback) args[i];
                        break;
                    }
                }
            }
            if (args.length != typeAry.length) {
                throw new Exception("argument count error!");
            }
            RemotingProtocol<?> message = new RemotingProtocol<>(localaddress, sequence.getAndIncrement());

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

            if (sync) {
                RemotingNettyClient.getInstance().invokeASync(address, message, 2000, callback);
                return null;
            }
            message = RemotingNettyClient.getInstance().invokeSync(address, message, 2000);
            
            //            Future<Object> fu = executor.submit(new InvokeThread(address, message));
            //            Object result = fu.get();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("response: %s", JSON.toJSONString(message)));
            }
            //            logger.info(String.format("invoke sync use:%s", watch.toString()));
            return message.getResultVal();
        } catch (Exception e) {
            logger.error("invoke error", e);
            throw e;
        }
    }
}

@SuppressWarnings("rawtypes")
class InvokeThread implements Callable<Object> {

    private String address;
    private RemotingProtocol message;

    public InvokeThread(final String address, final RemotingProtocol message) {
        this.address = address;
        this.message = message;
    }

    @Override
    public Object call() throws Exception {
        RemotingProtocol msg = RemotingNettyClient.getInstance().invokeSync(address, message, 3000);
        return msg.getResultVal();
    }
}
