package com.takin.rpc.remoting.netty4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyServerHandler extends SimpleChannelInboundHandler<RemotingProtocol> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RemotingProtocol msg) throws Exception {
        
    }

}
