package com.takin.rpc.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.takin.emmet.util.CollectionUtil;
import com.takin.rpc.server.ServiceInfos.SessionBean;

import io.netty.util.internal.StringUtil;

@Singleton
public class ServiceInfosHolder {

    private static final Logger logger = LoggerFactory.getLogger(ServiceInfosHolder.class);

    private final Multimap<String, SessionBean> serviceimplmap = ArrayListMultimap.create();

    private final HashMap<Class<?>, Class<?>> classMap = new HashMap<Class<?>, Class<?>>();

    @Inject
    private ServiceInfosHolder() {
        ServiceInfos serviceInfos = GuiceDI.getInstance(Scaner.class).getContractInfo();
        if (serviceInfos != null && CollectionUtil.isNotEmpty(serviceInfos.getSessionBeanList())) {
            for (SessionBean bean : serviceInfos.getSessionBeanList()) {
                logger.info(bean.getDefineName() + "-->" + bean.getImplClass().getCls().getName());
                serviceimplmap.put(bean.getDefineName(), bean);
            }
        }
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

        if (classMap.get(defineClass) != null) {
            return classMap.get(defineClass);
        }

        Collection<SessionBean> beans = serviceimplmap.get(defineClass.getName());
        if (CollectionUtil.isNotEmpty(beans)) {
            Iterator<SessionBean> beanite = beans.iterator();
            if (beans.size() == 1) {
                SessionBean bean = beanite.next();
                classMap.put(defineClass, bean.getImplClass().getCls());
                return bean.getImplClass().getCls();
            } else {
                while (beanite.hasNext()) {
                    SessionBean bean = beanite.next();
                    if (bean.isDefaultimpl()) {
                        classMap.put(defineClass, bean.getImplClass().getCls());
                        return bean.getImplClass().getCls();
                    }
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
        if (StringUtil.isNullOrEmpty(lookup)) {
            return null;
        }
        Collection<SessionBean> beans = serviceimplmap.get(defineClass.getName());
        if (CollectionUtil.isNotEmpty(beans)) {
            Iterator<SessionBean> beanite = beans.iterator();
            while (beanite.hasNext()) {
                SessionBean bean = beanite.next();
                if (lookup.toLowerCase().equals(bean.getLookup().toLowerCase())) {
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
