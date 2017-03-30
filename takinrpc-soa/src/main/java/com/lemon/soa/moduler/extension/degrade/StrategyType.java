package com.lemon.soa.moduler.extension.degrade;

/**
 * 策略类型
 * 
 * me
 */
public enum StrategyType {

    /**
     * 返回null
     */
    NULL,

    /**
     * 抛异常
     */
    EXCEPTION,

    /**
     * 本地mock服务
     */
    MOCK;

}
