package com.lemon.soa.moduler.neure;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * 容错支持类
 *
 * me
 */
public class HystrixSetterSupport {

    /**
     * Default FaultTolerance To HystrixCommand.Setter
     * 
     * @return
     */
    public static HystrixCommand.Setter buildSetter() {
        return buildSetter(new HystrixSetter());
    }

    /**
     * FaultTolerance To HystrixCommand.Setter
     *
     * @param ft
     * @return
     */
    @SuppressWarnings("deprecation")
    public static HystrixCommand.Setter buildSetter(HystrixSetter hs) {
        HystrixCommand.Setter setter = HystrixCommand.Setter
                        //Command分组KEY
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey(hs.getCommandGroupKey()));
        //Command KEY
        setter = setter.andCommandKey(HystrixCommandKey.Factory.asKey(hs.getCommandKey()));
        //线程池KEY
        setter = setter.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(hs.getThreadPoolKey()));

        //Command资源设置
        setter = setter.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        //超时控制
                        .withExecutionIsolationThreadTimeoutInMilliseconds(hs.getEitTimeout())//执行隔离线程超时毫秒,默认为1000ms
                        .withExecutionTimeoutInMilliseconds(hs.getEtimeout())//执行超时时间,默认为1000ms
                        //断路器
                        .withCircuitBreakerRequestVolumeThreshold(hs.getCbRequests())//当在配置时间窗口内达到此数量的失败后,进行短路,默认20个
                        .withCircuitBreakerSleepWindowInMilliseconds(hs.getCbSleepWindow())//短路多久以后开始尝试是否恢复,默认5s
                        .withCircuitBreakerErrorThresholdPercentage(hs.getCbErrorRate())//出错百分比阈值,当达到此阈值后,开始短路,默认50%
                        //调用线程允许请求HystrixCommand.GetFallback()的最大数量，默认10。超出时将会有异常抛出，注意：该项配置对于THREAD隔离模式也起作用
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(hs.getFismRequests()));

        //线程池设置
        setter = setter.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        //核心线程数
                        .withCoreSize(hs.getThreadPoolCoreSize())//线程池设置:线程池核心线程数,默认为10
                        //Queue
                        .withQueueSizeRejectionThreshold(hs.getThreadPoolQueueSize()));//排队线程数量阈值，默认为5，达到时拒绝，如果配置了该选项，队列的大小是该队列

        return setter;
    }

}