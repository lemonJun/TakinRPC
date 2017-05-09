package com.takin.rpc.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.takin.emmet.collection.CollectionUtil;
import com.takin.rpc.zkclient.IZkChildListener;
import com.takin.rpc.zkclient.ZkClient;
import com.takin.rpc.zkclient.ZkUtils;

/**
 * 通过zk 获取服务端地址
 *
 * @author WangYazhou
 * @date  2017年5月9日 下午12:25:12
 * @see
 */
public class ClientRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ClientRegistry.class);

    private final String homepath = "/takin/rpc/";

    private final Lock lock = new ReentrantLock();

    private final AtomicBoolean once = new AtomicBoolean();

    private ZkClient zkclient;
    private NamingFactory naming;

    ClientRegistry(NamingFactory naming) {
        this.naming = naming;
    }

    public void listen(String servername) {
        lock.lock();
        try {
            ServiceConfig config = naming.getConfig(servername);
            if (config.isUsezk()) {
                if (once.compareAndSet(false, true)) {
                    zkclient = new ZkClient(config.getZkhosts());
                }

                //初次注册  先获取children列表
                List<String> hosts = ZkUtils.getChildrenParentMayNotExist(zkclient, servertpath(servername));
                childrentoconfig(hosts, config);
                final List<String> children = new ArrayList<String>();
                //增加监听
                IZkChildListener listener = new IZkChildListener() {
                    public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                        children.clear();
                        if (currentChildren != null) {
                            children.addAll(currentChildren);
                        }
                        childrentoconfig(children, config);

                        logger.info("handle childchange " + parentPath + ", " + currentChildren);
                    }
                };

                zkclient.subscribeChildChanges(servertpath(servername), listener);
            }
        } catch (Exception e) {
            logger.error("registry server:{}", servername, e);
        } finally {
            lock.unlock();
        }
    }

    //把zk的列表转化为配置
    private void childrentoconfig(List<String> list, ServiceConfig config) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        //
        List<String> address = Lists.newArrayList();
        for (String child : list) {
            address.add(child.substring(0, child.lastIndexOf("/") + 1));
        }
    }

    private String servertpath(String servername) {
        return String.format("%s/%s", homepath, servername);
    }

}
