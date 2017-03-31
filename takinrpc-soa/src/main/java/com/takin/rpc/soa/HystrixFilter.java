package com.takin.rpc.soa;

import com.takin.rpc.remoting.IFilter;
import com.takin.rpc.remoting.netty5.RemotingContext;
import com.takin.rpc.remoting.netty5.RemotingProtocol;

@SuppressWarnings("rawtypes")
public class HystrixFilter implements IFilter {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void filter(RemotingContext context, RemotingProtocol protocol) throws Exception {
        TakinHystrixCommand command = new TakinHystrixCommand(context, protocol);
        command.execute();
    }

}
