package com.takin.rpc.server;

import com.takin.rpc.remoting.netty4.RemotingContext;

public interface IFilter {

    /**
     * 获得优先级
     * @return
     */
    public int getPriority();

    /**
     * 过虑
     * @param context
     * @return
     * @throws Exception
     */
    public Object filter(RemotingContext context) throws Exception;

}
