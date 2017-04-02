package com.takin.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.takin.rpc.remoting.netty5.RemotingProtocol;
import com.takin.rpc.remoting.netty5.ResponseFuture;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 客户端处理类
 * 
 * @author WangYazhou
 * @date  2017年2月12日 下午8:18:41
 * @see
 */
public class ResponseHandler extends ChannelHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Stopwatch watch = Stopwatch.createStarted();
        RemotingProtocol message = (RemotingProtocol) msg;
        final ResponseFuture responseFuture = RemotingNettyClient.responseTable.get(message.getOpaque());
        if (responseFuture != null) {
            responseFuture.putResponse(message);
            logger.debug(String.format("put resopnse  use:%s", watch.toString()));
        }
        RemotingNettyClient.responseTable.remove(message.getOpaque());
        logger.debug(String.format("client channel read use:%s", watch.toString()));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
