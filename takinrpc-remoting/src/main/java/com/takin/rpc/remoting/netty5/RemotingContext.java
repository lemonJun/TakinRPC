package com.takin.rpc.remoting.netty5;

import com.takin.rpc.remoting.GlobalContext;
import com.takin.rpc.remoting.StopWatch;

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

    private StopWatch stopWatch = new StopWatch();

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

    public StopWatch getStopWatch() {
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

    public void setStopWatch(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }

}