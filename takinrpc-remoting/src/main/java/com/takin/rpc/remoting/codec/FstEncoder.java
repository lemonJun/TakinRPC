package com.takin.rpc.remoting.codec;

import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.takin.rpc.remoting.netty5.RemotingProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class FstEncoder extends MessageToByteEncoder<RemotingProtocol> {
    private static final Logger logger = LoggerFactory.getLogger(FstEncoder.class);

    final FSTConfiguration config = FSTConfiguration.createDefaultConfiguration();

    @Override
    protected void encode(ChannelHandlerContext ctx, RemotingProtocol msg, ByteBuf out) throws Exception {
        final Stopwatch watch = Stopwatch.createStarted();

        byte[] data2 = config.asByteArray(msg);
        int dataLength = data2.length; //读取消息的长度
        out.writeInt(dataLength); //先将消息长度写入，也就是消息头
        out.writeBytes(data2); //消息体中包含我们要发送的数据
        logger.info(String.format("kyro encode use:%s", watch.toString()));
    }

}
