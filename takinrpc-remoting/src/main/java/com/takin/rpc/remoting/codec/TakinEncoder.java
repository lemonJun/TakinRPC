package com.takin.rpc.remoting.codec;

import com.takin.rpc.remoting.netty4.RemotingProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TakinEncoder extends MessageToByteEncoder<RemotingProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RemotingProtocol msg, ByteBuf out) throws Exception {

    }

}
