package com.takin.rpc.remoting.codec;

import java.util.List;

import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.takin.rpc.remoting.netty4.RemotingProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class FstDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(FstDecoder.class);

    public static ThreadLocal<FSTConfiguration> conf = new ThreadLocal<FSTConfiguration>() {
        public FSTConfiguration initialValue() {
            return FSTConfiguration.createDefaultConfiguration();
        }
    };

    @SuppressWarnings("rawtypes")
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final Stopwatch watch = Stopwatch.createStarted();

        in.markReaderIndex(); //我们标记一下当前的readIndex的位置
        int dataLength = in.readInt();
        byte[] body = new byte[dataLength]; //传输正常
        in.readBytes(body);
        //        byte[] rawbyte = Snappy.uncompress(body);

        RemotingProtocol object2 = (RemotingProtocol) conf.get().asObject(body);
        out.add(object2);
        logger.info("fst decoder convert use:" + watch.toString());
    }

}
