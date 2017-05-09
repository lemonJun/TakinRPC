package com.takin.rpc.server.registry;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.takin.emmet.util.DateUtils;
import com.takin.rpc.server.NettyServerConfig;
import com.takin.rpc.zkclient.ZkClient;
import com.takin.rpc.zkclient.ZkUtils;

/**
 * 对服务端来说    只生成临时节点就可以了
 *
 * @author WangYazhou
 * @date  2017年5月9日 下午12:04:12
 * @see
 */
@Singleton
public class ServerRegistry extends AbstractService {

    private static final Logger logger = LoggerFactory.getLogger(ServerRegistry.class);

    private ZkClient zkclient;

    private final String homepath = "/takin/rpc";

    private NettyServerConfig serverconfig;

    @Inject
    private ServerRegistry(final NettyServerConfig serverconfig) {
        this.serverconfig = serverconfig;

    }

    @Override
    protected void doStart() {
        if (serverconfig.isUsezk()) {
            try {
                zkclient = new ZkClient(serverconfig.getZkhosts());
                ZkUtils.makeSurePersistentPathExists(zkclient, parentpath());
                ZkUtils.createEphemeralPathExpectConflict(zkclient, mypath(), DateUtils.formatYMD_HMS(new Date()));
                logger.info("zk registry succ: " + mypath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doStop() {
        logger.info("zk registry stop:");
    }

    private String parentpath() {
        return String.format("%s/%s", homepath, serverconfig.getServername());
    }

    private String mypath() {
        return String.format("%s/%s/%s", homepath, serverconfig.getServername(), serverconfig.getUUID());
    }

}
