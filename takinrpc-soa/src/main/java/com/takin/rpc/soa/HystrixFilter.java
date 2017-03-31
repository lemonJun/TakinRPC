package com.takin.rpc.soa;

import com.takin.rpc.remoting.netty5.RemotingContext;
import com.takin.rpc.server.IFilter;
import com.takin.rpc.server.anno.FilterAnno;

public class HystrixFilter implements IFilter {

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public Object filter(RemotingContext context) throws Exception {
        TakinHystrixCommand command = new TakinHystrixCommand(context);
        return command.execute();
    }

}
