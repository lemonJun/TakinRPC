package com.takin.rpc.server.tcc;

import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.takin.emmet.util.Tuple;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.Scaner;
import com.takin.rpc.server.ServiceInfos;

/**
 * 
 *
 * @author WangYazhou
 * @date  2017年5月8日 下午12:07:41
 * @see
 */
@Singleton
public class TccProvider {

    private final ConcurrentHashMap<Class<?>, Tuple<String, String>> compenMap = new ConcurrentHashMap<Class<?>, Tuple<String, String>>();

    @Inject
    private TccProvider() {

    }

    /**
     * 初始化接口服务的事务补偿关系
     */
    public void init() {
        ServiceInfos serviceInfos = GuiceDI.getInstance(Scaner.class).getContractInfo();

    }

    //判断此接口服务是否需要补偿 
    public boolean isServiceCompensable(Class<?> clazz) {
        return compenMap.get(clazz) != null;
    }

    //获取补偿关系
    public Tuple<String, String> getCompensable(Class<?> clazz) {

        return compenMap.get(clazz);
    }

}
