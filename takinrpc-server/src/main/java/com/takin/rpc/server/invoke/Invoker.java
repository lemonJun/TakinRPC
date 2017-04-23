package com.takin.rpc.server.invoke;

import com.google.inject.ImplementedBy;
import com.takin.rpc.remoting.netty4.RemotingProtocol;

@ImplementedBy(value = CGlibInvoker.class)
public interface Invoker {
    public abstract Object invoke(RemotingProtocol msg) throws Exception;

}
