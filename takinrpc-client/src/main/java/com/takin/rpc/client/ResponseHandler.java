//package com.takin.rpc.client;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.takin.rpc.remoting.netty4.RemotingProtocol;
//import com.takin.rpc.remoting.netty4.ResponseFuture;
//
//import io.netty.channel.ChannelHandlerAdapter;
//import io.netty.channel.ChannelHandlerContext;
//
///**
// * 客户端处理类
// * 
// * @author WangYazhou
// * @date  2017年2月12日 下午8:18:41
// * @see
// */
//public class ResponseHandler extends ChannelHandlerAdapter {
//
//    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
//
//    @SuppressWarnings("rawtypes")
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        long start = System.currentTimeMillis();
//
//        RemotingProtocol message = (RemotingProtocol) msg;
//        final ResponseFuture responseFuture = RemotingNettyClient.responseTable.get(message.getOpaque());
//        logger.info(String.format("getfuture from table use:%s pad:%d", responseFuture.getWatch().toString(), (System.currentTimeMillis() - start)));
//
//        if (responseFuture != null) {
//            responseFuture.putResponse(message);
//            logger.info(String.format("put resopnse use:%s pad:%d", responseFuture.getWatch().toString(), (System.currentTimeMillis() - start)));
//        }
//        RemotingNettyClient.responseTable.remove(message.getOpaque());
//        logger.info(String.format("client channel read use:%s", responseFuture.getWatch().toString()));
//    }
//
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        cause.printStackTrace();
//        ctx.close();
//    }
//}
