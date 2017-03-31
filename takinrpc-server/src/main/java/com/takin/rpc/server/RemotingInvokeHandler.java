package com.takin.rpc.server;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.takin.rpc.remoting.GlobalContext;
import com.takin.rpc.remoting.netty5.RemotingContext;
import com.takin.rpc.remoting.netty5.RemotingProtocol;
import com.takin.rpc.server.invoke.Invoker;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * 接收客户端发起的请求   并按
 * 
 * 
 * @author lemon
 * @version 1.0
 * @date  2015年10月14日 下午4:09:22
 * @see   
 * @since
 */
public class RemotingInvokeHandler extends ChannelHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RemotingInvokeHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        RemotingProtocol msg = (RemotingProtocol) obj;
        try {
            Stopwatch watch = Stopwatch.createStarted();
            //            if (logger.isDebugEnabled()) {
            //                logger.debug("REQUEST: " + JSON.toJSONString(msg));
            //            }

            RemotingContext context = new RemotingContext(ctx);
            GlobalContext.getSingleton().setThreadLocal(context);

            Object result = GuiceDI.getInstance(Invoker.class).invoke(msg);
            msg.setResultJson(result);

            //            if (logger.isDebugEnabled()) {
            //                logger.debug("RESPONSE: " + JSON.toJSONString(msg));
            //            }
            logger.info(String.format("server invoke use:%s", watch.toString()));
        } catch (Exception e) {
            logger.error("netty server invoke error", e);
            throw e;
        } finally {
            GlobalContext.getSingleton().removeThreadLocal();
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelRegistered ");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("welcom channelActive");
        super.channelActive(ctx);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        logger.info("welcom bind");
        super.bind(ctx, localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        logger.info("welcom connect");
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("welcom channelUnregistered");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("welcom channelInactive");
        super.channelInactive(ctx);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        logger.info("welcom disconnect");
        super.disconnect(ctx, promise);
    }

}
