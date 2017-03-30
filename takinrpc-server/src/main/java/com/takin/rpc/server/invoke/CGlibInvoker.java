package com.takin.rpc.server.invoke;

import javax.inject.Singleton;

import com.takin.rpc.remoting.exception.NoImplClassException;
import com.takin.rpc.remoting.netty5.RemotingProtocol;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.ServiceInfosHolder;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

@Singleton
public class CGlibInvoker implements Invoker {
    
    @Override
    public Object invoke(RemotingProtocol msg) throws Exception {
        String methodName = msg.getMethod();
        Object[] args = msg.getArgs();
        Class<?>[] mParamsType = msg.getmParamsTypes();
        Class<?> implClass = GuiceDI.getInstance(ServiceInfosHolder.class).getImplClass(msg.getDefineClass(), msg.getImplClass());

        if (implClass == null) {
            throw new NoImplClassException(msg.getDefineClass().getName());
        }
        FastClass fastClazz = FastClass.create(implClass);
        // fast class反射调用  
        Object target = fastClazz.newInstance();

        FastMethod method = fastClazz.getMethod(msg.getMethod(), msg.getmParamsTypes());
        return method.invoke(target, args);
    }

}
