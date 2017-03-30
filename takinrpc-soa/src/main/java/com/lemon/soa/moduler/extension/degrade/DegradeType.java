package com.lemon.soa.moduler.extension.degrade;

/**
 * 服务降级分类
 * 
 * me
 */
public enum DegradeType {

    /**
     * 屏蔽降级
     */
    SHIELDING,

    /**
     * 容错降级
     */
    FAULTTOLERANT,

    /**
     * 业务降级
     */
    BUSINESS;

}
