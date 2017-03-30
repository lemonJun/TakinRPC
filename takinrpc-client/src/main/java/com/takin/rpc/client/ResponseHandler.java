package com.takin.rpc.client;

import org.apache.log4j.Logger;

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

    private static final Logger logger = Logger.getLogger(ResponseHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RemotingProtocol message = (RemotingProtocol) msg;
        logger.info(message.getResultJson());

        final ResponseFuture responseFuture = RemotingNettyClient.responseTable.get(message.getOpaque());

        if (responseFuture != null) {
            responseFuture.putResponse(message);
        }
        RemotingNettyClient.responseTable.remove(message.getOpaque());
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
