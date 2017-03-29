package com.lemon.takinmq.remoting.netty5;

import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.lemon.takinmq.common.anno.ImplementBy;
import com.lemon.takinmq.common.util.SerializeUtil;
import com.lemon.takinmq.remoting.GlobalContext;
import com.takin.emmet.string.StringUtil;

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
            logger.info("REQUEST: " + JSON.toJSONString(msg));
            RemotingContext context = new RemotingContext(ctx);
            GlobalContext.getSingleton().setThreadLocal(context);
            String clazzName = msg.getClazz();
            String methodName = msg.getMethod();
            Object[] args = msg.getArgs();
            Class<?>[] mParamsType = msg.getmParamsTypes();
            logger.info(String.format("invoke class:%s method:%s params:%s", clazzName, methodName, args != null ? Joiner.on(",").join(args) : ""));
            Class<?> mc[] = null;
            if (args != null) {//存在
                mc = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    Class<?> primc = args[i].getClass();
                    mc[i] = primc;
                }
            }
            if (StringUtil.isNotEmpty(clazzName)) {
                //反射调用
                Class<?> clazz = Class.forName(clazzName);
                if (clazz.isAnnotationPresent(ImplementBy.class)) {
                    ImplementBy impl = (ImplementBy) clazz.getAnnotation(ImplementBy.class);
                    Object target = getOjbectFromClass(impl.implclass());
                    Method[] methods = target.getClass().getDeclaredMethods();
                    //                    for (Method m : methods) {
                    //                        logger.info(m.toString());
                    //                    }
                    Method method = getMethod(target, methodName, mParamsType, mc);
                    if (method != null) {
                        method.setAccessible(true);
                        Object result = method.invoke(target, args);
                        if (!method.getReturnType().getName().equals("void")) {
                            msg.setResultJson(SerializeUtil.jsonSerialize(result));
                        }
                    } else {
                        msg.setResultJson("");
                    }
                }
            } else {
                msg.setResultJson("no class name content");
            }
            logger.info("RESPONSE: " + JSON.toJSONString(msg));
        } catch (Exception e) {
            logger.error("netty server handler error", e);
        } finally {
            GlobalContext.getSingleton().removeThreadLocal();
            ctx.writeAndFlush(msg);
        }
        return;
    }

    //根据参数获取指定的方法
    private Method getMethod(Object target, String methodName, Class<?>[] mParamsType, Class<?>[] mParamsType2) {
        Method method = null;
        try {
            method = target.getClass().getDeclaredMethod(methodName, mParamsType);
            logger.info(String.format("get method:%s succ params:%s", methodName, Joiner.on(",").join(mParamsType)));
        } catch (Exception e) {
            logger.error(String.format("get method:%s is null params:%s", methodName, Joiner.on(",").join(mParamsType)));
        }
        if (method == null) {
            try {
                method = target.getClass().getDeclaredMethod(methodName, mParamsType2);
                logger.info(String.format("get method:%s succ params:%s", methodName, Joiner.on(",").join(mParamsType2)));
            } catch (Exception e) {
                logger.error(String.format("get method:%s is null params:%s", methodName, Joiner.on(",").join(mParamsType2)));
            }
        }
        return method;
    }

    //获取实现类
    private Object getOjbectFromClass(String clazz) {
        logger.info("implclass:" + clazz);
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
