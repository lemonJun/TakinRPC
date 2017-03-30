package com.lemon.soa.moduler.neure;

import com.netflix.hystrix.HystrixCommand.Setter;

/**
 * 模块配置
 * 
 * me
 */
public class NeureEntity {

    private boolean neureSwitch = true;
    /**
     * Log4j threadContext
     */
    private boolean threadContext = true;
    /**
     * 降级开关
     */
    private boolean fallback = true;
    /**
     * 最大重试次数
     */
    private int maxRetryTimes = 0;
    /**
     * Hystrix设置
     */
    private HystrixSetter hystrixSetter;
    /**
     * 容错配置
     */
    private Setter setter = HystrixSetterSupport.buildSetter();

    public boolean isNeureSwitch() {
        return neureSwitch;
    }

    public void setNeureSwitch(boolean neureSwitch) {
        this.neureSwitch = neureSwitch;
    }

    public boolean isThreadContext() {
        return threadContext;
    }

    public void setThreadContext(boolean threadContext) {
        this.threadContext = threadContext;
    }

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public HystrixSetter getHystrixSetter() {
        return hystrixSetter;
    }

    public void setHystrixSetter(HystrixSetter hystrixSetter) {
        this.hystrixSetter = hystrixSetter;
    }

    public Setter getSetter() {
        return setter;
    }

    public void setSetter(Setter setter) {
        this.setter = setter;
    }

}
