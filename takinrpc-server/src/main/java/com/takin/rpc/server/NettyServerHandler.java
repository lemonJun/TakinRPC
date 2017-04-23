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

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RemotingProtocol msg) throws Exception {
        try {
            Stopwatch watch = Stopwatch.createStarted();
            //            if (logger.isDebugEnabled()) {
            //                logger.debug("REQUEST: " + JSON.toJSONString(msg));
            //            }

            RemotingContext context = new RemotingContext(ctx, msg);
            GlobalContext.getSingleton().setThreadLocal(context);

            Object result = null;//GuiceDI.getInstance(FilterChain.class).dofilter(context);
            if (result == null) {
                result = GuiceDI.getInstance(Invoker.class).invoke(msg);
            }
            msg.setResultVal(result);
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

}
