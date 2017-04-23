package com.takin.rpc.remoting.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.takin.rpc.remoting.netty4.RemotingProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@SuppressWarnings("rawtypes")
public class JsonEncode extends MessageToByteEncoder<RemotingProtocol> {

    private static final Logger logger = LoggerFactory.getLogger(JsonEncode.class);

    private final String encoding = "UTF-8";

    @Override
    protected void encode(ChannelHandlerContext ctx, RemotingProtocol msg, ByteBuf out) throws Exception {
        final Stopwatch watch = Stopwatch.createStarted();

        byte[] body = JSON.toJSONString(msg).getBytes(encoding);//将对象转换为byte
        int dataLength = body.length; //读取消息的长度
        out.writeInt(dataLength); //先将消息长度写入，也就是消息头
        out.writeBytes(body);
        logger.info("fastjson encode use:" + watch.toString());

    }

}
