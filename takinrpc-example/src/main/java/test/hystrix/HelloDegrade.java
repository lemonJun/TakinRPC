package test.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.takin.rpc.client.ProxyFactory;

import test.Hello;

public class HelloDegrade extends HystrixCommand<String> {

    final Hello helloService = ProxyFactory.create(Hello.class, "test", null, null);

    public HelloDegrade() {
        super(setter());
    }

    private static Setter setter() {
        HystrixCommandGroupKey groupkey = HystrixCommandGroupKey.Factory.asKey("rpc");
        HystrixCommandKey commandkey = HystrixCommandKey.Factory.asKey("say");
        HystrixThreadPoolKey threadpoolkey = HystrixThreadPoolKey.Factory.asKey("hello-1");
        HystrixThreadPoolProperties.Setter threadproperties = HystrixThreadPoolProperties.Setter()//
                        .withCoreSize(20).withKeepAliveTimeMinutes(5).withMaxQueueSize(1000).withQueueSizeRejectionThreshold(100);

        HystrixCommandProperties.Setter commandproperty = HystrixCommandProperties.Setter()//
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)//
                        .withFallbackEnabled(true).withFallbackIsolationSemaphoreMaxConcurrentRequests(100)//
                        .withExecutionIsolationThreadInterruptOnFutureCancel(true)//
                        .withExecutionIsolationThreadInterruptOnTimeout(true)//
                        .withExecutionTimeoutEnabled(true).withExecutionTimeoutInMilliseconds(1000);
        return HystrixCommand.Setter.withGroupKey(groupkey).andCommandKey(commandkey)//
                        .andThreadPoolKey(threadpoolkey).andThreadPoolPropertiesDefaults(threadproperties)//
                        .andCommandPropertiesDefaults(commandproperty);
    }

    @Override
    protected String run() throws Exception {
        return helloService.say("helo");
    }

    @Override
    protected String getFallback() {
        return "right";
    }

}
