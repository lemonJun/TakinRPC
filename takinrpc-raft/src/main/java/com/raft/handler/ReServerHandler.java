package com.raft.handler;

import com.raft.*;
import com.raft.domain.RequestInfo;
import com.raft.domain.ResponseInfo;
import com.raft.future.BasicFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by Administrator on 2016/10/31.
 */
public class ReServerHandler extends ChannelInboundHandlerAdapter {
    private RequestManager requestManager;
    private ChannelManager channelManager;
    private TimerManager timerManager;
    private TaskManager taskManager;
    private final RaftOperator raftOperator;
    private final boolean isServer;

    public ReServerHandler(RequestManager requestManager, ChannelManager channelManager, TimerManager timerManager, TaskManager taskManager, boolean isServer) {
        this.requestManager = requestManager;
        this.channelManager = channelManager;
        this.timerManager = timerManager;
        this.taskManager = taskManager;

        this.raftOperator = new RaftOperator(this.requestManager, this.channelManager);

        this.isServer = isServer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!isServer) {
            channelManager.addChannel(ctx.channel().remoteAddress().toString(), ctx.channel());
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("shit");
        if (msg instanceof RequestInfo) {
            RequestInfo reqInfo = (RequestInfo) msg;

            //对req返回的处理
            raftOperator.invoke(reqInfo, ctx.channel());
        } else if (msg instanceof ResponseInfo) {
            ResponseInfo respInfo = (ResponseInfo) msg;

            //对resp返回的处理
            raftOperator.invoke(respInfo);
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        channelManager.removeChannel(ctx.channel().remoteAddress().toString());
        super.exceptionCaught(ctx, cause);
    }
}
