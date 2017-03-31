package com.takin.rpc.server.invoke;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.takin.emmet.reflect.RMethodUtils;
import com.takin.rpc.remoting.exception.NoImplClassException;
import com.takin.rpc.remoting.netty5.RemotingProtocol;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.ServiceInfosHolder;

@Singleton
public class JDKInvoker implements Invoker {

    private static final Logger logger = LoggerFactory.getLogger(JDKInvoker.class);

    private final ConcurrentHashMap<String, Method> methodCache = new ConcurrentHashMap<String, Method>();

    @Inject
    public JDKInvoker() {

    }

    @Override
    public Object invoke(RemotingProtocol msg) throws Exception {
        String methodName = msg.getMethod();
        Object[] args = msg.getArgs();
        Class<?>[] mParamsType = msg.getmParamsTypes();
        Class<?> implClass = GuiceDI.getInstance(ServiceInfosHolder.class).getImplClass(msg.getDefineClass(), msg.getImplClass());

        if (implClass == null) {
            throw new NoImplClassException(msg.getDefineClass().getName());
        }
        String mkey = String.format("%s_%s", implClass.getSimpleName(), methodName);

        Method method = methodCache.get(mkey);
        if (method == null) {
            method = RMethodUtils.searchMethod(implClass, methodName, mParamsType);
            logger.info(String.format("search method:%s", methodName));
            methodCache.putIfAbsent(mkey, method);
        }

        if (method == null) {
            throw new NoImplClassException(msg.getDefineClass().getName());
        }
        Object target = GuiceDI.getInstance(ServiceInfosHolder.class).getOjbectFromClass(implClass.getName());

        //此步反射 非常耗时 
        if (method != null) {
            method.setAccessible(true);
            return method.invoke(target, args);
        }
        return "";
    }

}
