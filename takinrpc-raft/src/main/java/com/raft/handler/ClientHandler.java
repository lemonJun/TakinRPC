package com.raft.handler;

import com.raft.RaftOperator;
import com.raft.ServerNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Random;

/**
 * Created by Administrator on 2016/10/28.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client active");
        //        ctx.writeAndFlush(Unpooled.copiedBuffer("client : fuck you".getBytes()));
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String operate = new String(bytes);
        buf.release();

        if (operate.equals("voteToMe")) {
            //            String val = operate();
            //            ctx.writeAndFlush(Unpooled.copiedBuffer(val.getBytes()));
            ctx.writeAndFlush(Unpooled.copiedBuffer("ok".getBytes()));
        } else if (operate.equals("ping")) {
            RaftOperator.responseHeartBeat();
        }

        super.channelRead(ctx, msg);
    }

    public String operate() {
        int val = new Random().nextInt(3);
        if (val > 1) {
            return "ok";
        } else {
            return "no";
        }
    }
}
