//package com.lemon.takinmq.remoting.clientproxy;
//
//import java.lang.reflect.Method;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.apache.log4j.Logger;
//
//import com.lemon.takinmq.remoting.util.GenericsUtils;
//
//import javassist.util.proxy.MethodHandler;
//import javassist.util.proxy.ProxyFactory;
//import javassist.util.proxy.ProxyObject;
//
//public class JavassistProxy {
//
//    private static final Logger logger = Logger.getLogger(JavassistProxy.class);
//
//    private static final ConcurrentHashMap<String, ProxyObject> proxyMap = GenericsUtils.newConcurrentHashMap();
//
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public static <T> T createProxy(Class clazz, String str) {
//        ProxyObject proxy = proxyMap.get(str);
//        if (proxy != null) {
//            return (T) proxy;
//        }
//        ProxyFactory factory = new ProxyFactory();
//        factory.setInterfaces(new Class[] { clazz });
//        Class proxyClass = factory.createClass();
//        try {
//            proxy = (ProxyObject) proxyClass.newInstance();
//            proxy.setHandler(new MethodHandler() {
//                @Override
//                public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
//                    logger.info("javassist invoke");
//                    return thisMethod.invoke(clazz, args);
//                }
//            });
//            if (proxy != null) {
//                proxyMap.put(str, proxy);
//            }
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return (T) proxy;
//    }
//}
