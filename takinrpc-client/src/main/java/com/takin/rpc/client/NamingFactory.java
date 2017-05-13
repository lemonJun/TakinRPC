package com.takin.rpc.client;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.takin.emmet.file.PropertiesHelper;

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

    private static final Logger logger = LoggerFactory.getLogger(NamingFactory.class);

    //存储服务配置
    private final ConcurrentHashMap<String, ServiceConfig> serviceconfigs = new ConcurrentHashMap<String, ServiceConfig>();

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
        ClientRegistry registry = new ClientRegistry(this);

        File directory = new File("conf/services");
        String[] services = directory.list(new IndexFileNameFilter());

        for (String str : services) {
            try {
                PropertiesHelper pro = new PropertiesHelper("conf/services/" + str);
                ServiceConfig config = new ServiceConfig();
                config.setServicename(pro.getString("server.name"));
                config.setServiceid(pro.getInt("server.id", "0"));
                config.setUsezk(pro.getBoolean("use.zk"));
                config.setZkhosts(pro.getString("zk.hosts"));
                serviceconfigs.put(config.getServicename(), config);
                config.setAddress(Splitter.on(",").splitToList(pro.getString("server.hosts")));
                registry.listen(config.getServicename());
                
                logger.info(JSON.toJSONString(config));
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    public ServiceConfig getConfig(String serviceName) {
        return serviceconfigs.get(serviceName);
    }

    //更新配置
    public void putConfig(ServiceConfig config) {
        serviceconfigs.put(config.getServicename(), config);
    }

    public List<String> getConfigAddr(String serviceName) {
        return serviceconfigs.get(serviceName).getAddress();
    }

    final class IndexFileNameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            if (name.endsWith(".properties")) {
                return true;
            }
            return false;
        }

    }

}
