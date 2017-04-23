package com.takin.rpc.remoting.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.takin.rpc.remoting.netty4.RemotingProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class JsonDecode extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(JsonDecode.class);

    private final String encoding = "UTF-8";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final Stopwatch watch = Stopwatch.createStarted();

        in.markReaderIndex(); //我们标记一下当前的readIndex的位置
        int dataLength = in.readInt(); // 读取传送过来的消息的长度。ByteBuf 的readInt()方法会让他的readIndex增加4
        byte[] body = new byte[dataLength]; //传输正常
        in.readBytes(body);
        RemotingProtocol o = JSON.parseObject(new String(body, encoding), RemotingProtocol.class); //将byte数据转化为我们需要的对象
        out.add(o);
        logger.info("fastjson decoder use:" + watch.toString());
        
    }

}
