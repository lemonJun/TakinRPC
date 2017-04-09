//package com.raft;
//
//import com.raft.domain.RequestInfo;
//
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//
///**
// * Created by Administrator on 2016/10/31.
// */
//public class TestHandler extends ChannelInboundHandlerAdapter {
//
//    public TestHandler() {
//        System.out.println("testHandler");
//    }
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("active");
//        RequestInfo requestInfo = new RequestInfo();
//        requestInfo.setRequestId(1);
//        requestInfo.setService("test");
//        ctx.writeAndFlush(requestInfo);
//
//        super.channelActive(ctx);
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("shit");
//        System.out.println(msg instanceof RequestInfo);
//        super.channelRead(ctx, msg);
//    }
//}
