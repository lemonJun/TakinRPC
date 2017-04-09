package com.raft.handler;

import com.raft.ChannelManager;
import com.raft.RaftOperator;
import com.raft.ServerNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;

/**
 * Created by Administrator on 2016/10/28.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private ChannelManager channelManager;

    public ServerHandler(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String ret = new String(bytes);
        buf.release();

        if (ret.equals("voteToMe")) {
            String val = operate(ctx.channel().remoteAddress().toString());
            ctx.writeAndFlush(Unpooled.copiedBuffer(val.getBytes()));
        } else if (ret.equals("ping")) {
            RaftOperator.responseHeartBeat();
        } else if ("ok".equals(ret)) {
            System.out.println(ctx.channel().remoteAddress() + "已经投了我一票了");
            RaftOperator.responseVote();
        }

        //        ctx.writeAndFlush(Unpooled.copiedBuffer("server : you fuck".getBytes()));
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server active");
        channelManager.addChannel(ctx.channel().remoteAddress().toString(), ctx.channel());
        ServerNode.serverIncr();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        channelManager.removeChannel(ctx.channel().remoteAddress().toString());
        super.exceptionCaught(ctx, cause);
    }

    public String operate(String addr) {
        int val = new Random().nextInt(3);
        if (val > 1) {
            System.out.println("我把票投给了" + addr);
            return "ok";
        } else {
            return "no";
        }
    }
}
