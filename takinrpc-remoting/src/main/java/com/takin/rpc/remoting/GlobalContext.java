package com.takin.rpc.remoting;

import com.takin.rpc.remoting.netty5.RemotingContext;

/**
 * 主要存放每个线程请求的上下文
 * 
 */
public class GlobalContext {

    private ThreadLocal<RemotingContext> threadLocal = new ThreadLocal<RemotingContext>();

    private static final Object lockHelper = new Object();

    private static volatile GlobalContext m_global = null;

    /**
     * 获取单例Global
     * @return
     */
    public static GlobalContext getSingleton() {
        if (m_global == null) {
            synchronized (lockHelper) {
                if (m_global == null) {
                    m_global = new GlobalContext();
                }
            }
        }
        return m_global;
    }

    private GlobalContext() {
    }

    public ThreadLocal<RemotingContext> getThreadLocal() {
        return threadLocal;
    }

    public RemotingContext getFromThreadLocal() {
        return (RemotingContext) GlobalContext.getSingleton().getThreadLocal().get();
    }

    public void setThreadLocal(RemotingContext context) {
        GlobalContext.getSingleton().getThreadLocal().set(context);
    }

    public void removeThreadLocal() {
        GlobalContext.getSingleton().getThreadLocal().remove();
    }

}