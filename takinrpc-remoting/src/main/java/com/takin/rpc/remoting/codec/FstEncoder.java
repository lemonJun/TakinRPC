package com.takin.rpc.remoting.codec;

import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.takin.rpc.remoting.netty4.RemotingProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@SuppressWarnings("rawtypes")
public class FstEncoder extends MessageToByteEncoder<RemotingProtocol> {
    private static final Logger logger = LoggerFactory.getLogger(FstEncoder.class);

    public static ThreadLocal<FSTConfiguration> conf = new ThreadLocal<FSTConfiguration>() {
        public FSTConfiguration initialValue() {
            return FSTConfiguration.createDefaultConfiguration();
        }
    };

    @Override
    protected void encode(ChannelHandlerContext ctx, RemotingProtocol msg, ByteBuf out) throws Exception {
        final Stopwatch watch = Stopwatch.createStarted();

        byte[] data2 = conf.get().asByteArray(msg);
        //        byte[] compdata = Snappy.compress(data2);
        int dataLength = data2.length; //读取消息的长度
        //        ByteBuffer buf = ByteBuffer.allocate(dataLength);
        //        buf.putInt(dataLength);
        //        buf.put(data2);
        //        out.writeBytes(buf);
        out.writeInt(dataLength); //先将消息长度写入，也就是消息头
        out.writeBytes(data2); //消息体中包含我们要发送的数据

        logger.info(String.format("fst encode use:%s", watch.toString()));
    }

}
