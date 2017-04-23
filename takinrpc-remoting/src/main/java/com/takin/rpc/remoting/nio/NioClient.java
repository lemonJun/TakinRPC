package com.takin.rpc.remoting.nio;

import java.net.SocketAddress;

import com.takin.rpc.remoting.nio.channel.ChannelInitializer;
import com.takin.rpc.remoting.nio.config.NioClientConfig;
import com.takin.rpc.remoting.nio.handler.Futures;
import com.takin.rpc.remoting.nio.handler.NioHandler;
import com.takin.rpc.remoting.nio.processor.NioClientProcessor;

public class NioClient {

    private NioClientProcessor processor;

    public NioClient(NioClientConfig clientConfig, NioHandler eventHandler, ChannelInitializer channelInitializer) {
        this.processor = new NioClientProcessor(clientConfig, eventHandler, channelInitializer);
    }

    public Futures.ConnectFuture connect(SocketAddress remoteAddress) {

        processor.start();

        return processor.connect(remoteAddress);
    }

    public void shutdownGracefully() {
    }
}
