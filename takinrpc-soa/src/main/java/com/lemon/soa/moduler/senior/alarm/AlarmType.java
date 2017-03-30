package com.lemon.soa.moduler.senior.alarm;

public enum AlarmType implements IAlarmType {

    /**
     * 容错降级
     */
    DEGRADE_FAULTTOLERANT,

    /**
     * 并发控制溢出
     */
    FLOWRATE_CTT_OVERFLOW,

    /**
     * 流速控制溢出
     */
    FLOWRATE_QPS_OVERFLOW,

    /**
     * 常规路由失败告警
     */
    RUN_ROUTE,
    /**
     * 呼吸失败告警
     */
    RUN_BREATH,
    /**
     * Fault-Tolerant失败告警
     */
    FALLBACK_FAULT_TOLERANT,
    /**
     * 回调失败告警
     */
    CALLBACK;

}
