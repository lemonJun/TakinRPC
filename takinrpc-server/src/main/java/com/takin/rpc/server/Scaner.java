package com.takin.rpc.server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.takin.rpc.server.ServiceInfos.SessionBean;
import com.takin.rpc.server.anno.FilterAnno;
import com.takin.rpc.server.anno.InitAnno;
import com.takin.rpc.server.anno.ServiceDefine;
import com.takin.rpc.server.anno.ServiceImpl;
import com.takin.rpc.server.anno.ServiceMethod;

@Singleton
@SuppressWarnings("rawtypes")
public class Scaner {
    private static final Logger logger = LoggerFactory.getLogger(Scaner.class);
    private ServiceInfos contractInfo = null;
    private final List<ClassInfo> contractClassInfos = Lists.newArrayList();;
    private final List<ClassInfo> behaviorClassInfos = Lists.newArrayList();
    private final List<Class<?>> filterList = Lists.newArrayList();
    private final List<Class<?>> initList = Lists.newArrayList();

    private final AtomicBoolean once = new AtomicBoolean(false);

    @Inject
    private Scaner() {

    }

    /**
     * 从jar中扫描出服务类
     * @param path
     * @param classLoader
     * @return
     * @throws Exception
     */

    public void scanInfo(DynamicClassLoader classLoader) throws Exception {
        if (once.compareAndSet(false, true)) {
            scan(classLoader);
            for (Class<?> cls : filterList) {
                logger.info(String.format("add filter:%s", cls.getSimpleName()));
            }
            for (Class<?> cls : initList) {
                logger.info(String.format("add initer:%s", cls.getSimpleName()));
            }
        }
    }

    /**
     * scan jars create ContractInfo
     * @param path
     * @param classLoader
     * @return
     * @throws Exception
     */
    private void scan(DynamicClassLoader classLoader) throws Exception {
        List<String> jarPathList = classLoader.getJarList();

        for (String jpath : jarPathList) {
            logger.info("scan jar at:" + jpath);
            Set<Class<?>> clsSet = null;
            try {
                clsSet = ClassHelper.getClassFromJar(jpath, classLoader);
            } catch (Exception ex) {
                throw ex;
            }
            if (clsSet == null) {
                continue;
            }
            for (Class<?> cls : clsSet) {
                try {
                    FilterAnno filter = cls.getAnnotation(FilterAnno.class);
                    if (filter != null) {
                        filterList.add(cls);
                    }
                    InitAnno initer = cls.getAnnotation(InitAnno.class);
                    if (initer != null) {
                        initList.add(cls);
                    }

                    ServiceImpl behavior = cls.getAnnotation(ServiceImpl.class);
                    ServiceDefine contract = cls.getAnnotation(ServiceDefine.class);
                    if (behavior == null && contract == null) {
                        continue;
                    }
                    if (contract != null) {
                        ClassInfo ci = contract(cls);
                        if (ci != null) {
                            contractClassInfos.add(ci);
                        }
                    } else if (behavior != null) {
                        ClassInfo ci = behavior(cls);
                        if (ci != null) {
                            behaviorClassInfos.add(ci);
                        }
                    }
                } catch (Exception ex) {
                    throw ex;
                }
            }
        }
        contractInfo = createContractInfo(contractClassInfos, behaviorClassInfos);
        logger.info("finish scan jar");
    }

    /**
     * 
     * @param cls
     * @param ignoreAnnotation
     * @return
     */
    protected ClassInfo contract(Class<?> cls, boolean ignoreAnnotation) {
        if (ignoreAnnotation) {
            ClassInfo<?> ci = new ClassInfo();
            ci.setCls(cls);
            ci.setClassType(ClassInfo.ClassType.INTERFACE);

            Method[] methods = cls.getDeclaredMethods();
            List<ClassInfo.MethodInfo> methodInfos = new ArrayList<ClassInfo.MethodInfo>();

            for (Method m : methods) {
                if (Modifier.isPublic(m.getModifiers()) || Modifier.isProtected(m.getModifiers())) {
                    ClassInfo.MethodInfo mi = new ClassInfo.MethodInfo();
                    mi.setMethod(m);
                    methodInfos.add(mi);
                }
            }
            ci.setMethodList(methodInfos);

            return ci;
        } else {
            return contract(cls);
        }
    }

    /**
     * 
     * @param cls
     * @return
     */
    protected ClassInfo contract(Class<?> cls) {
        ServiceDefine contractAnn = cls.getAnnotation(ServiceDefine.class);

        ClassInfo<?> ci = new ClassInfo();
        ci.setCls(cls);
        ci.setClassType(ClassInfo.ClassType.INTERFACE);

        List<Class<?>> interfaceList = getInterfaces(cls);
        List<ClassInfo.MethodInfo> methodInfos = new ArrayList<ClassInfo.MethodInfo>();

        for (Class<?> interfaceCls : interfaceList) {
            Method[] methods = interfaceCls.getDeclaredMethods();
            if (contractAnn != null && contractAnn.defaultAll()) {
                for (Method m : methods) {
                    if (Modifier.isPublic(m.getModifiers()) || Modifier.isProtected(m.getModifiers())) {
                        ClassInfo.MethodInfo mi = new ClassInfo.MethodInfo();
                        mi.setMethod(m);
                        methodInfos.add(mi);
                    }
                }
            } else {
                for (Method m : methods) {
                    if (Modifier.isPublic(m.getModifiers()) || Modifier.isProtected(m.getModifiers())) {
                        ServiceMethod oc = m.getAnnotation(ServiceMethod.class);
                        if (oc != null) {
                            ClassInfo.MethodInfo mi = new ClassInfo.MethodInfo();
                            mi.setMethod(m);
                            methodInfos.add(mi);
                        }
                    }
                }
            }
        }

        ci.setMethodList(methodInfos);

        return ci;

    }

    /**
     * 
     * @param cls
     * @return
     * @throws Exception
     */
    protected ClassInfo behavior(Class<?> cls) throws Exception {
        ServiceImpl behaviorAnn = cls.getAnnotation(ServiceImpl.class);

        ClassInfo ci = new ClassInfo();
        ci.setCls(cls);
        ci.setClassType(ClassInfo.ClassType.CLASS);

        if (behaviorAnn != null && !behaviorAnn.lookUP().equalsIgnoreCase("")) {
            ci.setLookUP(behaviorAnn.lookUP());
        } else {
            ci.setLookUP(cls.getSimpleName());
        }
        Method[] methods = cls.getDeclaredMethods();
        List<ClassInfo.MethodInfo> methodInfos = new ArrayList<ClassInfo.MethodInfo>();

        for (Method m : methods) {
            //only load public or protected method
            if (Modifier.isPublic(m.getModifiers()) || Modifier.isProtected(m.getModifiers())) {
                ClassInfo.MethodInfo mi = new ClassInfo.MethodInfo();
                mi.setMethod(m);

                Class<?>[] paramAry = m.getParameterTypes();
                Type[] types = m.getGenericParameterTypes();

                String[] paramNames = ClassHelper.getParamNames(cls, m);
                String[] mapping = new String[paramAry.length];

                ClassInfo.ParamInfo[] paramInfoAry = new ClassInfo.ParamInfo[paramAry.length];
                for (int i = 0; i < paramAry.length; i++) {
                    paramInfoAry[i] = new ClassInfo.ParamInfo(i, paramAry[i], types[i], paramNames[i], mapping[i]);
                }
                mi.setParamInfoAry(paramInfoAry);

                methodInfos.add(mi);
            }
        }
        ci.setMethodList(methodInfos);

        return ci;
    }

    /**
     * create ContractInfo from contracts, behaviors
     * @param contracts
     * @param behaviors
     * @return
     */

    private ServiceInfos createContractInfo(List<ClassInfo> contracts, List<ClassInfo> behaviors) {
        ServiceInfos contractInfo = new ServiceInfos();
        List<SessionBean> sessionBeanList = new ArrayList<SessionBean>();
        for (ClassInfo c : contracts) {
            SessionBean bean = new SessionBean();
            bean.setDefineClass(c);
            bean.setDefineName(c.getCls().getName());
            Map<String, String> implMap = new HashMap<String, String>();

            for (ClassInfo b : behaviors) {
                Class<?>[] interfaceAry = b.getCls().getInterfaces();
                for (Class<?> item : interfaceAry) {
                    if (item == c.getCls()) {
                        implMap.put(b.getLookUP(), b.getCls().getName());
                        bean.setImplClass(b);
                        break;
                    }
                }
            }
            bean.setInstanceMap(implMap);
            sessionBeanList.add(bean);
        }

        contractInfo.setSessionBeanList(sessionBeanList);
        return contractInfo;
    }

    /**
     * get all interfaces
     * @param cls
     * @return
     */
    private List<Class<?>> getInterfaces(Class<?> cls) {
        List<Class<?>> clsList = new ArrayList<Class<?>>();
        getInterfaces(cls, clsList);
        return clsList;
    }

    /**
     * get all interfaces
     * @param cls
     * @param clsList
     */
    private void getInterfaces(Class<?> cls, List<Class<?>> clsList) {
        clsList.add(cls);
        Class<?>[] aryCls = cls.getInterfaces();

        if (aryCls != null && aryCls.length > 0) {
            for (Class<?> c : aryCls) {
                getInterfaces(c, clsList);
            }
        }
    }

    public ServiceInfos getContractInfo() {
        return contractInfo;
    }

    public List<Class<?>> getFilterList() {
        return filterList;
    }

    public List<Class<?>> getInitList() {
        return initList;
    }

}