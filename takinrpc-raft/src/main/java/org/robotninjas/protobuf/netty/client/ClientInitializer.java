package org.robotninjas.protobuf.netty.client;

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

import io.netty.util.concurrent.EventExecutorGroup;
import org.robotninjas.protobuf.netty.NettyRpcProto;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.util.concurrent.ConcurrentHashMap;

class ClientInitializer<T extends SocketChannel> extends ChannelInitializer<T> {

    private final EventExecutorGroup eventExecutor;

    ClientInitializer(EventExecutorGroup eventExecutor) {
        this.eventExecutor = eventExecutor;
    }

    @Override
    protected void initChannel(T ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        p.addLast("protobufDecoder", new ProtobufDecoder(NettyRpcProto.RpcContainer.getDefaultInstance()));

        p.addLast("frameEncoder", new LengthFieldPrepender(4));
        p.addLast("protobufEncoder", new ProtobufEncoder());

        ConcurrentHashMap<Integer, RpcCall> callMap = new ConcurrentHashMap<Integer, RpcCall>();
        p.addLast(eventExecutor, "inboundHandler", new InboundHandler(callMap));
        p.addLast("outboundHandler", new OutboundHandler(callMap));

    }

}
