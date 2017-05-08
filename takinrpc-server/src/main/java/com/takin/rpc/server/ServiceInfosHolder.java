package com.takin.rpc.server;

import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.takin.emmet.collection.CollectionUtil;
import com.takin.rpc.server.ServiceInfos.SessionBean;

@Singleton
public class ServiceInfosHolder {

    private static final Logger logger = LoggerFactory.getLogger(ServiceInfosHolder.class);

    @Inject
    private ServiceInfosHolder() {
    }

    /** 
     * 
     * 获得一个接口的实现类
     * @param defineClass 接口类
     * @param implClass  多实现类的情况下   客户端可以指定   一般都是null 
     * @return
     */
    public Class<?> getImplClass(Class<?> defineClass, Class<?> implClass) {
        if (implClass != null) {
            return implClass;
        }
        ServiceInfos serviceInfos = GuiceDI.getInstance(Scaner.class).getContractInfo();
        if (serviceInfos != null && CollectionUtil.isNotEmpty(serviceInfos.getSessionBeanList())) {
            for (SessionBean bean : serviceInfos.getSessionBeanList()) {
                if (bean.getDefineClass().getCls().getName().equals(defineClass.getName())) {
                    return bean.getImplClass().getCls();
                }
            }
        }
        return null;
    }

    /**
     * 获取一个接口的实现类
     * @param defineClass
     * @param lookup  指定名称的实现类
     * @return
     */
    public Class<?> getImplClass(Class<?> defineClass, String lookup) {
        ServiceInfos serviceInfos = GuiceDI.getInstance(Scaner.class).getContractInfo();
        if (serviceInfos != null && CollectionUtil.isNotEmpty(serviceInfos.getSessionBeanList())) {
            for (SessionBean bean : serviceInfos.getSessionBeanList()) {
                if (bean.getDefineClass().getCls().getName().equals(defineClass.getName())) {
                    return bean.getImplClass().getCls();
                }
            }
        }
        return null;
    }

    private final ConcurrentHashMap<String, Object> implMap = new ConcurrentHashMap<String, Object>();

    //
    public Object getOjbectFromClass(String clazz) {
        if (implMap.get(clazz) == null) {
            synchronized (ServiceInfosHolder.class) {
                if (implMap.get(clazz) == null) {
                    try {
                        //此处需要无参的构造器
                        Object obj = Class.forName(clazz).newInstance();
                        logger.info(String.format("create target object:%s", clazz));
                        implMap.putIfAbsent(clazz, obj);
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            }
        }
        return implMap.get(clazz);
    }

}
