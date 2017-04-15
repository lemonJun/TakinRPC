package com.takin.rpc.registry;

import com.github.zkclient.ZkClient;

public class ServerRegistryInit {

    private ZkClient zkclient;

    private final String homepath = "takinrpc";

    public void init(String zkhosts) {
        zkclient = new ZkClient(zkhosts);
    }
    
}
