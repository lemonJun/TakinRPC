package com.takin.rpc.server.invoke;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.takin.rpc.remoting.exception.NoImplClassException;
import com.takin.rpc.remoting.netty5.RemotingProtocol;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.ServiceInfosHolder;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

@Singleton
public class CGlibInvoker implements Invoker {

    private static final Logger logger = LoggerFactory.getLogger(CGlibInvoker.class);

    @Inject
    public CGlibInvoker() {
    } 
    
    @Override
    public Object invoke(RemotingProtocol msg) throws Exception {
        Stopwatch watch = Stopwatch.createStarted();
        Object[] args = msg.getArgs();
        Class<?> implClass = GuiceDI.getInstance(ServiceInfosHolder.class).getImplClass(msg.getDefineClass(), msg.getImplClass());

        if (implClass == null) {
            throw new NoImplClassException(msg.getDefineClass().getName());
        }
        FastClass fastClazz = FastClass.create(implClass);
        // fast class反射调用  
        Object target = fastClazz.newInstance();

        FastMethod method = fastClazz.getMethod(msg.getMethod(), msg.getmParamsTypes());
        Object obj = method.invoke(target, args);
        logger.info(String.format("cglib invoke use:%s", watch.toString()));
        return obj;
    }

}
