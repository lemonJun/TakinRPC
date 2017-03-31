package com.takin.rpc.remoting.netty5;

import com.takin.rpc.remoting.GlobalContext;
import com.takin.rpc.remoting.MyStopWatch;

import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * 
 * @author WangYazhou
 * @date  2017年2月8日 下午5:29:26
 * @see
 */
public class RemotingContext {

    private ChannelHandlerContext context;

    private boolean monitor;

    private MyStopWatch stopWatch = new MyStopWatch();

    private Throwable error;

    private boolean isDoInvoke = true;

    public RemotingContext(ChannelHandlerContext ctx) {
        this.context = ctx;
    }

    /**
     * 从ThreadLocal里获取SCFContext
     * @return
     */
    public static RemotingContext getFromThreadLocal() {
        return GlobalContext.getSingleton().getThreadLocal().get();
    }

    public boolean isMonitor() {
        return monitor;
    }

    public void setMonitor(boolean monitor) {
        this.monitor = monitor;
    }

    public MyStopWatch getStopWatch() {
        return stopWatch;
    }

    public void setDoInvoke(boolean isDoInvoke) {
        this.isDoInvoke = isDoInvoke;
    }

    public boolean isDoInvoke() {
        return isDoInvoke;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Throwable getError() {
        return error;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }

    public void setStopWatch(MyStopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }

}