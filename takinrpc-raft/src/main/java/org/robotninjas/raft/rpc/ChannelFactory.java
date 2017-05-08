package org.robotninjas.raft.rpc;

import com.googlecode.protobuf.netty.NettyRpcChannel;
import com.googlecode.protobuf.netty.NettyRpcClient;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.robotninjas.raft.ReplicaInfo;

import java.net.InetAddress;
import java.net.InetSocketAddress;

class ChannelFactory extends BaseKeyedPoolableObjectFactory<ReplicaInfo, NettyRpcChannel> {

    private final NettyRpcClient client;

    public ChannelFactory(NettyRpcClient client) {
        this.client = client;
    }

    @Override
    public NettyRpcChannel makeObject(ReplicaInfo key) throws Exception {
        InetAddress addr = InetAddress.getByName(key.getHostAndPort().getHostText());
        InetSocketAddress sockAddr = new InetSocketAddress(addr, key.getHostAndPort().getPort());
        return client.blockingConnect(sockAddr);
    }

    @Override
    public void destroyObject(ReplicaInfo key, NettyRpcChannel obj) throws Exception {
        obj.close();
    }

    @Override
    public boolean validateObject(ReplicaInfo key, NettyRpcChannel obj) {
        return obj.isOpen();
    }
}
