package com.lemon.soa.moduler.extension.flowrate;

public class FlowRateConf {

    /**
     * 流控总开关
     */
    public static final String FLOWRATE_SWITCH_KEY = "switch";
    public static final boolean FLOWRATE_SWITCH_DEF_VAL = false;

    /**
     * 并发子开关
     */
    public static final String CCT_SWITCH_KEY = "cctswitch";
    public static final boolean CCT_SWITCH_DEF_VAL = false;
    public static final String CCT_NUM_KEY = "cctNum";
    public static final int CCT_NUM_DEF_VAL = 20;

    /**
     * QPS子开关
     */
    public static final String QPS_SWITCH_KEY = "qpsswitch";
    public static final boolean QPS_SWITCH_DEF_VAL = false;
    public static final String QPS_NUM_KEY = "qpsNum";
    public static final double QPS_NUM_DEF_VAL = 100;

}
