package com.takin.rpc.soa;

import javax.naming.spi.DirStateFactory.Result;
import javax.xml.ws.spi.Invoker;

import com.takin.rpc.remoting.IFilter;
import com.takin.rpc.remoting.netty5.RemotingContext;
import com.takin.rpc.remoting.netty5.RemotingProtocol;

public class HystrixFilter implements IFilter {

    @Override
    public int getPriority() {
        return 0;
    }
    
    @Override
    public void filter(RemotingContext context, RemotingProtocol protocol) throws Exception {

    }

}
