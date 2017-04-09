package com.raft.handler;

import com.raft.RaftManager2;
import com.raft.domain.RequestInfo;
import com.raft.domain.ResponseInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by Administrator on 2016/11/8.
 */
public class ServerHandler2 extends ChannelInboundHandlerAdapter {
    private RaftManager2 raftManager;
    private boolean isServer;

    public ServerHandler2(RaftManager2 raftManager, boolean isServer) {
        this.raftManager = raftManager;
        this.isServer = isServer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!isServer) {
            raftManager.getChannelManager().addChannel(ctx.channel().remoteAddress().toString(), ctx.channel());
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("shit");
        if (msg instanceof RequestInfo) {
            RequestInfo reqInfo = (RequestInfo) msg;

            //对req返回的处理
            raftManager.getRaftOperator().invoke(reqInfo, ctx.channel());
        } else if (msg instanceof ResponseInfo) {
            ResponseInfo respInfo = (ResponseInfo) msg;

            //对resp返回的处理
            raftManager.getRaftOperator().invoke(respInfo);
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        raftManager.getChannelManager().removeChannel(ctx.channel().remoteAddress().toString());
        super.exceptionCaught(ctx, cause);
    }
}
