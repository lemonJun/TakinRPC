package com.takin.rpc.remoting.nio.channel;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import com.takin.rpc.remoting.nio.codec.Decoder;
import com.takin.rpc.remoting.nio.codec.Encoder;
import com.takin.rpc.remoting.nio.handler.Futures;

/**
 * @author Robert HG (254963746@qq.com) on 1/24/16.
 */
public interface NioChannel {

    long getId();

    SocketAddress remoteAddress();

    SocketAddress localAddress();

    Futures.WriteFuture writeAndFlush(Object msg);

    Futures.CloseFuture close();

    Futures.CloseFuture getCloseFuture();

    boolean isConnected();

    boolean isOpen();

    boolean isClosed();

    SocketChannel socketChannel();

    void setLastReadTime(long lastReadTime);

    void setLastWriteTime(long lastWriteTime);

    Decoder getDecoder();

    Encoder getEncoder();
}
