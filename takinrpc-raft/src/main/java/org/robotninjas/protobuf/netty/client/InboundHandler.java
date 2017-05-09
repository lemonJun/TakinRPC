package org.robotninjas.protobuf.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.robotninjas.protobuf.netty.NoRequestIdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static org.robotninjas.protobuf.netty.NettyRpcProto.RpcContainer;
import static org.robotninjas.protobuf.netty.NettyRpcProto.RpcResponse;

class InboundHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelInboundHandlerAdapter.class);
    private final ConcurrentHashMap<Integer, RpcCall> callMap;

    InboundHandler(ConcurrentHashMap<Integer, RpcCall> callMap) {
        this.callMap = callMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        checkArgument(msg instanceof RpcContainer);
        RpcContainer container = (RpcContainer) msg;
        checkArgument(container.hasResponse());

        RpcResponse response = container.getResponse();
        RpcCall call = callMap.remove(response.getId());
        if (call == null) {
            throw new NoRequestIdException();
        }
        call.complete(response);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("Exception caught", cause);
        if (cause instanceof IOException) {
            synchronized (callMap) {
                for (RpcCall call : callMap.values()) {
                    call.fail(cause);
                }
                callMap.clear();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        synchronized (callMap) {
            for (RpcCall call : callMap.values()) {
                call.cancel(true);
            }
            callMap.clear();
        }
    }

}
