package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.takin.rpc.remoting.netty5.RemotingContext;
import com.takin.rpc.server.IFilter;

public class HelloFilter implements IFilter {

    private static final Logger logger = LoggerFactory.getLogger(HelloFilter.class);

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public Object filter(RemotingContext context) throws Exception {
        logger.info(JSON.toJSONString(context));
        return null;
    }

}
