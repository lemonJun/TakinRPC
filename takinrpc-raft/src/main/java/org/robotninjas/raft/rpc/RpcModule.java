package org.robotninjas.raft.rpc;

import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.googlecode.protobuf.netty.NettyRpcClient;
import com.googlecode.protobuf.netty.NettyRpcServer;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.util.concurrent.Executors;

public class RpcModule extends PrivateModule {

    private final int numWorkerThreads;

    public RpcModule(int numWorkerThreads) {
        this.numWorkerThreads = numWorkerThreads;
    }

    @Override
    protected void configure() {

        bind(RpcClientFactory.class);
        expose(RpcClientFactory.class);

    }

    @Provides
    @Singleton
    @Exposed
    NettyRpcClient getRpcClient() {
        NioClientSocketChannelFactory channelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool(), numWorkerThreads);
        return new NettyRpcClient(channelFactory);
    }

    @Provides
    @Singleton
    @Exposed
    NettyRpcServer getRpcServer() {
        NioServerSocketChannelFactory channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool(), numWorkerThreads);
        return new NettyRpcServer(channelFactory);
    }

}
