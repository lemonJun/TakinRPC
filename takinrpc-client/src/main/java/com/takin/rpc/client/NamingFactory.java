package com.takin.rpc.client;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.collect.ImmutableList;

/**
 * 
 * 生成配置信息   
 * 按指定算法获取配置信息 
 *
 * @author WangYazhou
 * @date  2017年3月30日 下午2:18:09
 * @see 
 */
public class NamingFactory {

    //存储服务配置
    private final ConcurrentHashMap<String, ServiceConfig> serviceconfigs = new ConcurrentHashMap<String, ServiceConfig>();

    private AtomicBoolean once = new AtomicBoolean(false);

    public static volatile NamingFactory instance;

    public static NamingFactory getInstance() {
        if (instance == null) {
            synchronized (NamingFactory.class) {
                if (instance == null) {
                    instance = new NamingFactory();
                }
            }
        }
        return instance;
    }

    private NamingFactory() {
        initServiceConfig();
    }

    public void initServiceConfig() {
        ServiceConfig config = new ServiceConfig();
        config.setServicename("test");
        config.setAddress(ImmutableList.of("127.0.0.1:6781", "127.0.0.1:6782"));
        serviceconfigs.put(config.getServicename(), config);
    }

    public ServiceConfig getConfig(String serviceName) {
        return serviceconfigs.get(serviceName);
    }

    public List<String> getConfigAddr(String serviceName) {
        return serviceconfigs.get(serviceName).getAddress();
    }

}
