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

    private ServiceInfos serviceInfos;

    @Inject
    public ServiceInfosHolder() {
    }

    public ServiceInfos getServiceInfos() {
        return serviceInfos;
    }

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

    private final ConcurrentHashMap<String, Object> implMap = new ConcurrentHashMap<String, Object>();

    //
    public Object getOjbectFromClass(String clazz) {
        if (implMap.get(clazz) == null) {
            synchronized (RemotingInvokeHandler.class) {
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
