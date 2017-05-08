package com.takin.rpc.server.tcc;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.takin.emmet.collection.CollectionUtil;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.Scaner;
import com.takin.rpc.server.ServiceInfos;
import com.takin.rpc.server.ServiceInfos.SessionBean;

/**
 * 
 *
 * @author WangYazhou
 * @date  2017年5月8日 下午12:07:41
 * @see
 */
@Singleton
public class TccProvider {

    private static final Logger logger = LoggerFactory.getLogger(TccProvider.class);

    private final Multimap<Class<?>, Tcc> compenMap = ArrayListMultimap.create();

    @SuppressWarnings("unchecked")
    @Inject
    private TccProvider() {
        ServiceInfos serviceInfos = GuiceDI.getInstance(Scaner.class).getContractInfo();
        List<SessionBean> sessionBeanList = serviceInfos.getSessionBeanList();
        if (CollectionUtil.isNotEmpty(sessionBeanList)) {
            for (SessionBean bean : sessionBeanList) {
                logger.info(JSON.toJSONString(bean));
                if (bean.getImplClass().getCls().isAnnotationPresent(Compensable.class)) {
                    Compensable compenanno = (Compensable) bean.getImplClass().getCls().getAnnotation(Compensable.class);
                    logger.info(JSON.toJSONString(compenanno));
                    compenMap.put(compenanno.interfaceClass(), new Tcc(compenanno.cancellableKey(), compenanno.confirmableKey()));
                }
            }
        }
    }

    //判断此接口服务是否需要补偿 
    public boolean isServiceCompensable(Class<?> clazz) {
        return compenMap.get(clazz) != null;
    }

    //获取补偿关系
    public Tcc getCompensable(Class<?> clazz) {
        Collection<Tcc> tccs = compenMap.get(clazz);
        if (tccs != null) {
            Iterator<Tcc> ite = tccs.iterator();
            while (ite.hasNext()) {
                return ite.next();
            }
        }
        return null;
    }

    public static class Tcc {
        private String cancel;
        private String confirm;

        public Tcc(String concel, String confirm) {
            this.cancel = concel;
            this.confirm = confirm;
        }

        public String getCancel() {
            return cancel;
        }

        public void setCancel(String cancel) {
            this.cancel = cancel;
        }

        public String getConfirm() {
            return confirm;
        }

        public void setConfirm(String confirm) {
            this.confirm = confirm;
        }

    }

}
