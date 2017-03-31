package test;

import com.alibaba.fastjson.JSON;
import com.takin.rpc.remoting.netty5.RemotingContext;
import com.takin.rpc.server.IFilter;
import com.takin.rpc.server.anno.FilterAnno;

@FilterAnno
public class TestFilter implements IFilter {

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public Object filter(RemotingContext context) throws Exception {
        System.out.println(JSON.toJSON(context));
        return null;
    }

}
