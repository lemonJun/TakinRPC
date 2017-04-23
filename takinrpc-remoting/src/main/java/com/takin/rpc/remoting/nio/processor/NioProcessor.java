package com.takin.rpc.remoting.nio.processor;

import java.net.SocketAddress;
import java.nio.channels.SelectionKey;

import com.takin.rpc.remoting.nio.channel.NioChannel;
import com.takin.rpc.remoting.nio.handler.Futures;

/**
 * @author Robert HG (254963746@qq.com) on 1/24/16.
 */
public interface NioProcessor {

    void accept(SelectionKey key);

    Futures.WriteFuture writeAndFlush(NioChannel channel, Object msg);

    void flush(NioChannel channel);

    void read(NioChannel channel);

    Futures.ConnectFuture connect(SocketAddress remoteAddress);

    void connect(SelectionKey key);
}