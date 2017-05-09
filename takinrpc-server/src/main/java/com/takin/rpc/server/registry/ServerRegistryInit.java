package com.takin.rpc.server.registry;

import com.takin.rpc.zkclient.ZkClient;

public class ServerRegistryInit {

    private ZkClient zkclient;

    private final String homepath = "takinrpc";

    public void init(String zkhosts) {
        zkclient = new ZkClient(zkhosts);
    }

}
