package com.takin.rpc.soa;

import org.apache.log4j.Logger;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.takin.rpc.remoting.netty5.RemotingContext;
import com.takin.rpc.remoting.netty5.RemotingProtocol;

public class TakinHystrixCommand extends HystrixCommand {

    private static Logger logger = Logger.getLogger(TakinHystrixCommand.class);
    private static final int DEFAULT_THREADPOOL_CORE_SIZE = 30;
    private RemotingContext context;
    private RemotingProtocol protocol;

    public TakinHystrixCommand(RemotingContext context, RemotingProtocol protocol) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(protocol.getDefineClass().getName())).andCommandKey(HystrixCommandKey.Factory.asKey(String.format("%s_%d", protocol.getMethod(), protocol.getArgs() == null ? 0 : protocol.getArgs().length))).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerRequestVolumeThreshold(20)//10秒钟内至少19此请求失败，熔断器才发挥起作用
                        .withCircuitBreakerSleepWindowInMilliseconds(30000)//熔断器中断请求30秒后会进入半打开状态,放部分流量过去重试
                        .withCircuitBreakerErrorThresholdPercentage(50)//错误率达到50开启熔断保护
                        .withExecutionTimeoutEnabled(false))//使用dubbo的超时，禁用这里的超时
                        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(getThreadPoolCoreSize())));//线程池为30
        this.context = context;
        this.protocol = protocol;
    }

    private static int getThreadPoolCoreSize() {
        return DEFAULT_THREADPOOL_CORE_SIZE;
    }

    @Override
    protected Object run() throws Exception {
        return null;
    }

}
