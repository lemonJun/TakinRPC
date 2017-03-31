package com.takin.rpc.soa;

import com.takin.rpc.remoting.netty5.RemotingContext;
import com.takin.rpc.server.IFilter;

@SuppressWarnings("rawtypes")
public class HystrixFilter implements IFilter {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void filter(RemotingContext context) throws Exception {
        TakinHystrixCommand command = new TakinHystrixCommand(context);
        command.execute();
    }

}
