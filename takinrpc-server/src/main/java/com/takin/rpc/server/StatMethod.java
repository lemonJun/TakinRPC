package com.takin.rpc.server;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

public class StatMethod implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(StatMethod.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Stopwatch watch = Stopwatch.createStarted();
        invocation.getClass().getName();
        Object obj = invocation.proceed();
        logger.info(String.format("%s.%s use:%s", invocation.getClass().getName(), invocation.getMethod().getName(), watch.toString()));
        return obj;
    }

}
