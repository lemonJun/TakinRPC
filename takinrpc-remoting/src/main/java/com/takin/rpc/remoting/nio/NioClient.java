package com.takin.rpc.remoting.nio;

import java.net.SocketAddress;

import com.takin.rpc.remoting.nio.channel.ChannelInitializer;
import com.takin.rpc.remoting.nio.config.NioClientConfig;
import com.takin.rpc.remoting.nio.handler.Futures;
import com.takin.rpc.remoting.nio.handler.NioHandler;
import com.takin.rpc.remoting.nio.processor.NioClientProcessor;

/**
 * @author Robert HG (254963746@qq.com) on 1/30/16.
 */
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
        // TODO
    }
}
