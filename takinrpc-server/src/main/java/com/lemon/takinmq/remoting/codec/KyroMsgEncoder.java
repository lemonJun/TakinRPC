package com.lemon.takinmq.remoting.codec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Output;
import com.lemon.takinmq.remoting.netty5.RemotingProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class KyroMsgEncoder extends MessageToByteEncoder<RemotingProtocol> {

    private Kryo kryo = new Kryo();

    @Override
    protected void encode(ChannelHandlerContext ctx, RemotingProtocol msg, ByteBuf out) throws Exception {
        byte[] body = convertToBytes(msg); //将对象转换为byte
        int dataLength = body.length; //读取消息的长度
        out.writeInt(dataLength); //先将消息长度写入，也就是消息头
        out.writeBytes(body); //消息体中包含我们要发送的数据
    }

    private byte[] convertToBytes(RemotingProtocol car) {

        ByteArrayOutputStream bos = null;
        Output output = null;
        try {
            bos = new ByteArrayOutputStream();
            output = new Output(bos);
            kryo.writeObject(output, car);
            output.flush();

            return bos.toByteArray();
        } catch (KryoException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                output.close();
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
