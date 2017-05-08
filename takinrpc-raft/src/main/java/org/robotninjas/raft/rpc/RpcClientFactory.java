package org.robotninjas.raft.rpc;

import com.google.inject.Inject;
import com.googlecode.protobuf.netty.NettyRpcChannel;
import com.googlecode.protobuf.netty.NettyRpcClient;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.robotninjas.raft.ReplicaInfo;

public class RpcClientFactory {

    private final KeyedObjectPool<ReplicaInfo, NettyRpcChannel> clientCache;

    @Inject
    public RpcClientFactory(NettyRpcClient rpcClient) {
        GenericKeyedObjectPool.Config poolConfig = new GenericKeyedObjectPool.Config();
        poolConfig.testOnBorrow = true;
        poolConfig.testOnReturn = true;
        poolConfig.testWhileIdle = true;
        poolConfig.maxActive = 1;
        poolConfig.whenExhaustedAction = GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL;
        clientCache = new GenericKeyedObjectPool<ReplicaInfo, NettyRpcChannel>(new ChannelFactory(rpcClient), poolConfig);
    }

    public RpcClient newClient(ReplicaInfo info) {
        return new RemoteRpcClient(clientCache, info);
    }

}
