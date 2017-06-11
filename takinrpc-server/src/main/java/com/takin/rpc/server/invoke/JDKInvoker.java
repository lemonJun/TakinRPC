package com.takin.rpc.server.invoke;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.takin.emmet.reflect.RMethodUtils;
import com.takin.rpc.remoting.exception.NoImplClassException;
import com.takin.rpc.remoting.exception.NoImplMethodException;
import com.takin.rpc.remoting.netty4.RemotingProtocol;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.ServiceInfosHolder;

@Singleton
public class JDKInvoker implements Invoker {

    private static final Logger logger = LoggerFactory.getLogger(JDKInvoker.class);

    private final ConcurrentHashMap<String, Method> methodCache = new ConcurrentHashMap<String, Method>();

    @Inject
    public JDKInvoker() {

    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object invoke(RemotingProtocol msg) throws Exception {
        Stopwatch watch = Stopwatch.createStarted();

        String methodName = msg.getMethod();
        Object[] args = msg.getArgs();
        Class<?>[] mParamsType = msg.getmParamsTypes();
        Class<?> implClass = GuiceDI.getInstance(ServiceInfosHolder.class).getImplClass(msg.getDefineClass(), msg.getImplClass());

        if (implClass == null) {
            logger.error(String.format("define:%s impl:%s", msg.getDefineClass(), msg.getImplClass()));
            throw new NoImplClassException(msg.getDefineClass().getName());
        }
        String mkey = String.format("%s_%s", implClass.getSimpleName(), methodName);

        Method method = methodCache.get(mkey);
        if (method == null) {
            logger.info(String.format("method:%s args:%s", methodName, JSON.toJSONString(mParamsType)));
            method = RMethodUtils.searchMethod(implClass, methodName, mParamsType);
            if (method != null) {
                methodCache.putIfAbsent(mkey, method);
            }
        }

        if (method == null) {
            throw new NoImplMethodException(implClass.getName(), methodName);
        }

        Object target = GuiceDI.getInstance(ServiceInfosHolder.class).getOjbectFromClass(implClass.getName());

        Object retval = null;
        //此步反射 非常耗时 
        if (method != null) {
            method.setAccessible(true);
            retval = method.invoke(target, args);
        }
        if (logger.isDebugEnabled())
            logger.debug(String.format("jdk invoke use:%s", watch.toString()));
        return retval;
    }

}
