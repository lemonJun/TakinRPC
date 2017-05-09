/*
 * Copyright (c) 2009 Stephen Tu <stephen_tu@berkeley.edu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.robotninjas.protobuf.netty.client;

import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;

import javax.inject.Inject;
import java.net.SocketAddress;

public class RpcClient {

    private final Bootstrap bootstrap = new Bootstrap();

    <T extends SocketChannel> RpcClient(EventLoopGroup eventLoopGroup, EventExecutorGroup eventExecutor, Class<T> channel) {
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(channel);
        bootstrap.handler(new ClientInitializer<T>(eventExecutor));
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
    }

    @Inject
    public RpcClient(NioEventLoopGroup eventLoopGroup, EventExecutorGroup eventExecutor) {
        this(eventLoopGroup, eventExecutor, NioSocketChannel.class);
    }

    public RpcClient(OioEventLoopGroup eventLoopGroup, EventExecutorGroup eventExecutor) {
        this(eventLoopGroup, eventExecutor, OioSocketChannel.class);
    }

    public NettyRpcChannel connect(SocketAddress sa) throws InterruptedException {
        ChannelFuture f = bootstrap.connect(sa).await();
        return new NettyRpcChannel(f.channel());
    }

    public ListenableFuture<NettyRpcChannel> connectAsync(SocketAddress sa) {
        ChannelFuture channelFuture = bootstrap.connect(sa);
        NettyFutureAdapter adapter = new NettyFutureAdapter();
        channelFuture.addListener(adapter);
        return adapter;
    }

    static final class NettyFutureAdapter extends AbstractFuture<NettyRpcChannel> implements ChannelFutureListener {

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isDone() && future.isSuccess()) {
                set(new NettyRpcChannel(future.channel()));
            } else if (future.isDone() && future.cause() != null) {
                setException(future.cause());
            } else if (future.isDone() && future.isCancelled()) {
                cancel(false);
            }
        }
    }

}
