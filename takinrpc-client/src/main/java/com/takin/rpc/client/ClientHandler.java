package com.takin.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.rpc.remoting.netty4.RemotingProtocol;
import com.takin.rpc.remoting.netty4.ResponseFuture;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@SuppressWarnings("rawtypes")
public class ClientHandler extends SimpleChannelInboundHandler<RemotingProtocol> {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RemotingProtocol message) throws Exception {
        long start = System.currentTimeMillis();
        
        final ResponseFuture responseFuture = RemotingNettyClient.responseTable.get(message.getOpaque());
        logger.info(String.format("getfuture from table use:%s pad:%d", responseFuture.getWatch().toString(), (System.currentTimeMillis() - start)));

        if (responseFuture != null) {
            responseFuture.putResponse(message);
            logger.info(String.format("put resopnse use:%s pad:%d", responseFuture.getWatch().toString(), (System.currentTimeMillis() - start)));
        }
        RemotingNettyClient.responseTable.remove(message.getOpaque());
        logger.info(String.format("client channel read use:%s", responseFuture.getWatch().toString()));
    }

}
