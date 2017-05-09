package org.robotninjas.protobuf.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static org.robotninjas.protobuf.netty.NettyRpcProto.RpcContainer;

class OutboundHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelOutboundHandlerAdapter.class);
    private final ConcurrentHashMap<Integer, RpcCall> rpcMap;

    public OutboundHandler(ConcurrentHashMap<Integer, RpcCall> rpcMap) {
        this.rpcMap = rpcMap;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        checkArgument(msg instanceof RpcCall);

        RpcCall call = (RpcCall) msg;
        rpcMap.put(call.getRequest().getId(), call);
        ctx.writeAndFlush(RpcContainer.newBuilder().setRequest(call.getRequest()));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.debug("Exception caught", cause);
    }

}
