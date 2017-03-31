package com.takin.rpc.remoting;

import com.takin.rpc.remoting.netty5.RemotingContext;
import com.takin.rpc.remoting.netty5.RemotingProtocol;

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
    public void filter(RemotingContext context, RemotingProtocol protocol) throws Exception;

}
