package com.lemon.soa.moduler.neure;

/**
 * 执行类型
 * 
 * me
 */
public enum ExecuteType {

    /**
     * 常规路由服务成功
     */
    SUCCESS,
    /**
     * 常规路由服务失败
     */
    FAILURE,

    /******************************************/

    /**
     * 失败容错路由服务成功
     */
    FALLBACK_SUCCESS,
    /**
     * 失败容错路由服务失败
     */
    FALLBACK_FAILURE,

    /******************************************/

    /**
     * 呼吸服务成功
     */
    BREATHCYCLE_SUCCESS,
    /**
     * 呼吸服务失败
     */
    BREATHCYCLE_FAILURE,

    /**
     * 失败告警服务成功
     */
    ALARM_SUCCESS,
    /**
     * 失败告警服务失败
     */
    ALARM_FAILURE,

    /**
     * 回调服务成功
     */
    CALLBACK_SUCCESS,
    /**
     * 回调服务失败
     */
    CALLBACK_FAILURE,

}
