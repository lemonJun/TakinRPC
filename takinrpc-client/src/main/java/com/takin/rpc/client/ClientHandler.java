package com.takin.rpc.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.takin.rpc.remoting.netty4.RemotingProtocol;
import com.takin.rpc.remoting.netty4.ResponseFuture;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@SuppressWarnings("rawtypes")
public class ClientHandler extends SimpleChannelInboundHandler<RemotingProtocol> {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private ExecutorService callbackExecutor = Executors.newFixedThreadPool(1);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RemotingProtocol message) throws Exception {
        final ResponseFuture responseFuture = RemotingNettyClient.responseTable.get(message.getOpaque());
        //        logger.info(String.format("getfuture from table use:%s pad:%d", responseFuture.getWatch().toString(), (System.currentTimeMillis() - start)));
        if (responseFuture != null) {
            if (responseFuture.getInvokeCallback() != null) {
                callbackExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        logger.info(String.format("exec callback:%s", JSON.toJSONString(responseFuture)));
                        responseFuture.getInvokeCallback().operationComplete(message.getResultVal());
                    }
                });
            } else {
                responseFuture.putResponse(message);
            }
            //            logger.info(String.format("put resopnse use:%s pad:%d", responseFuture.getWatch().toString(), (System.currentTimeMillis() - start)));
        }
        RemotingNettyClient.responseTable.remove(message.getOpaque());
    }

}
