package com.takin.rpc.registry;

import com.github.zkclient.ZkClient;
import com.takin.emmet.string.StringUtil;
import com.takin.rpc.server.GuiceDI;
import com.takin.rpc.server.IInit;
import com.takin.rpc.server.NettyServerConfig;
import com.takin.rpc.server.anno.InitAnno;

@InitAnno(order = 2)
public class RegistryInit implements IInit {

    private ZkClient zkclient;

    private final String homepath = "takinrpc";

    @Override
    public void init() {

        NettyServerConfig serverconfig = GuiceDI.getInstance(NettyServerConfig.class);
        if (serverconfig.isUsezk() && StringUtil.isNotNullOrEmpty(serverconfig.getZkhosts())) {
            zkclient = new ZkClient(serverconfig.getZkhosts());
        }
    }

}
