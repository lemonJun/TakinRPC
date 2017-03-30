package com.takin.rpc.server;

import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.takin.emmet.reflect.RMethodUtils;
import com.takin.rpc.remoting.GlobalContext;
import com.takin.rpc.remoting.exception.NoImplClassException;
import com.takin.rpc.remoting.netty5.RemotingContext;
import com.takin.rpc.remoting.netty5.RemotingProtocol;

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

    private static final Logger logger = Logger.getLogger(RemotingInvokeHandler.class);

    //设置环境变量 
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        RemotingProtocol msg = (RemotingProtocol) obj;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("REQUEST: " + JSON.toJSONString(msg));
            }
            RemotingContext context = new RemotingContext(ctx);

            GlobalContext.getSingleton().setThreadLocal(context);
            String methodName = msg.getMethod();
            Object[] args = msg.getArgs();
            Class<?>[] mParamsType = msg.getmParamsTypes();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("invoke class:%s method:%s params:%s", msg.getDefineClass(), methodName, args != null ? Joiner.on(",").join(args) : ""));
            }
            Class<?> implClass = GuiceDI.getInstance(ServiceInfosHolder.class).getImplClass(msg.getDefineClass(), msg.getImplClass());

            if (implClass == null) {
                throw new NoImplClassException(msg.getDefineClass().getName());
            }

            Method method = RMethodUtils.searchMethod(implClass, methodName, mParamsType);

            if (method == null) {
                throw new NoImplClassException(msg.getDefineClass().getName());
            }
            Object target = getOjbectFromClass(implClass.getName());

            //此步反射 非常耗时
            if (method != null) {
                method.setAccessible(true);
                Object result = method.invoke(target, args);
                if (!method.getReturnType().getName().equals("void")) {
                    msg.setResultJson(result);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("RESPONSE: " + JSON.toJSONString(msg));
            }
        } catch (Exception e) {
            logger.error("netty server handler error", e);
            throw e;
        } finally {
            GlobalContext.getSingleton().removeThreadLocal();
            ctx.writeAndFlush(msg);
        }
        return;
    }

    //获取实现类
    private Object getOjbectFromClass(String clazz) {
        if (implMap.get(clazz) == null) {
            synchronized (RemotingInvokeHandler.class) {
                if (implMap.get(clazz) == null) {
                    try {
                        //此处需要无参的构造器
                        Object obj = Class.forName(clazz).newInstance();
                        implMap.put(clazz, obj);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
        }
        return implMap.get(clazz);
    }

    private final Map<String, Object> implMap = Maps.newConcurrentMap();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.debug("welcom channelRegistered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("welcom channelActive");
        super.channelActive(ctx);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        logger.debug("welcom bind");
        super.bind(ctx, localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        logger.debug("welcom connect");
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    //    @Override
    //    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    //        super.channelReadComplete(ctx);
    //    }
    //
    //    @Override
    //    public void flush(ChannelHandlerContext ctx) throws Exception {
    //        super.flush(ctx);
    //    }

}
