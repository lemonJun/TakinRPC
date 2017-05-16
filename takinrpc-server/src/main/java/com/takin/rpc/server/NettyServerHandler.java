package com.takin.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.takin.rpc.remoting.GlobalContext;
import com.takin.rpc.remoting.netty4.RemotingContext;
import com.takin.rpc.remoting.netty4.RemotingProtocol;
import com.takin.rpc.server.invoke.Invoker;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@SuppressWarnings("rawtypes")
public class NettyServerHandler extends SimpleChannelInboundHandler<RemotingProtocol> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private final Invoker invoker = GuiceDI.getInstance(Invoker.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RemotingProtocol msg) throws Exception {
        try {
            Stopwatch watch = Stopwatch.createStarted();
            logger.info(String.format("server invoke 0use:%s", watch.toString()));

            RemotingContext context = new RemotingContext(ctx, msg);
            GlobalContext.getSingleton().setThreadLocal(context);
            logger.info(String.format("server invoke 1use:%s", watch.toString()));

            Object result = null;//GuiceDI.getInstance(FilterChain.class).dofilter(context);
            if (result == null) {
                result = invoker.invoke(msg);
            }
            msg.setResultVal(result);
            logger.info(String.format("server invoke 2use:%s", watch.toString()));
        } catch (Exception e) {
            logger.error("netty server invoke error", e);
            throw e;
        } finally {
            GlobalContext.getSingleton().removeThreadLocal();
            ctx.writeAndFlush(msg);
        }
    }

}
