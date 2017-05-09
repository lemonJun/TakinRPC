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
package org.robotninjas.protobuf.netty.server;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AbstractService;
import com.google.inject.assistedinject.Assisted;
import com.google.protobuf.BlockingService;
import com.google.protobuf.Service;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelGroupFutureListener;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;

import javax.inject.Inject;
import java.net.SocketAddress;

public class RpcServer extends AbstractService {

    private final ServerBootstrap bootstrap;
    private final ChannelGroup allChannels;
    private final ServerHandler handler;
    private final SocketAddress address;

    <T extends ServerSocketChannel> RpcServer(EventLoopGroup eventLoopGroup, EventExecutorGroup eventExecutor, Class<T> channel, SocketAddress address) {
        this.address = address;
        this.allChannels = new DefaultChannelGroup(eventLoopGroup.next());
        this.handler = new ServerHandler(allChannels);
        this.bootstrap = new ServerBootstrap();
        bootstrap.channel(channel);
        bootstrap.childHandler(new ServerInitializer(eventExecutor, handler));
        bootstrap.group(eventLoopGroup);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
    }

    @Inject
    public RpcServer(NioEventLoopGroup eventLoopGroup, EventExecutorGroup eventExecutor, @Assisted SocketAddress address) {
        this(eventLoopGroup, eventExecutor, NioServerSocketChannel.class, address);
    }

    public RpcServer(OioEventLoopGroup eventLoopGroup, EventExecutorGroup eventExecutor, @Assisted SocketAddress address) {
        this(eventLoopGroup, eventExecutor, OioServerSocketChannel.class, address);
    }

    @Override
    protected void doStart() {
        try {
            ChannelFuture f = bootstrap.bind(address);
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        notifyStarted();
                    } else {
                        notifyFailed(future.cause());
                    }
                }
            });
        } catch (Throwable t) {
            notifyFailed(t);
            Throwables.propagate(t);
        }
    }

    @Override
    protected void doStop() {
        try {
            ChannelGroupFuture f = allChannels.close();
            f.addListener(new ChannelGroupFutureListener() {
                @Override
                public void operationComplete(ChannelGroupFuture future) throws Exception {
                    if (future.isSuccess()) {
                        notifyStopped();
                    } else {
                        notifyFailed(future.cause());
                    }
                }
            });
        } catch (Throwable t) {
            notifyFailed(t);
            Throwables.propagate(t);
        }
    }

    public void registerService(Service service) {
        handler.registerService(service);
    }

    public void unregisterService(Service service) {
        handler.unregisterService(service);
    }

    public void registerBlockingService(BlockingService service) {
        handler.registerBlockingService(service);
    }

    public void unregisterBlockingService(BlockingService service) {
        handler.unregisterBlockingService(service);
    }

}
