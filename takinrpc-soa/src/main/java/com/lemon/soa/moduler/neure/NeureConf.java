package com.lemon.soa.moduler.neure;

public class NeureConf {

    /**
     * 核心开关
     */
    public static final String SWITCH_KEY = "switch";
    public static final boolean SWITCH_DEF_VAL = true;

    /**
     * 容错开关
     */
    public static final String FALLBACK_SWITCH_KEY = "fallback";
    public static final boolean FALLBACK_SWITCH_DEF_VAL = true;

    /**
     * 线程之间参数传递开关
     */
    public static final String THREADCONTEXT_SWITCH_KEY = "threadContext";
    public static final boolean THREADCONTEXT_SWITCH_DEF_VAL = true;

    /**
     * 最大重试次数
     */
    public static final String MAX_RETRYTIMES_KEY = "maxRetryTimes";
    public static final int MAX_RETRYTIMES_DEF_VAL = 0;

    public static final String EITTIMEOUT_KEY = "eitTimeout";
    public static final int EITTIMEOUT_DEF_VAL = 80000;

    public static final String ETIMEOUT_KEY = "etimeout";
    public static final int ETIMEOUT_DEF_VAL = 80000;

    public static final String FISMREQUESTS_KEY = "fismRequests";
    public static final int FISMREQUESTS_DEF_VAL = 10;

    public static final String CBERRORRATE_KEY = "cbErrorRate";
    public static final int CBERRORRATE_DEF_VAL = 50;

    public static final String CBREQUESTS_KEY = "cbRequests";
    public static final int CBREQUESTS_DEF_VAL = 20;

    public static final String CBSLEEPWINDOW_KEY = "cbSleepWindow";
    public static final int CBSLEEPWINDOW_DEF_VAL = 5;

    public static final String THREADPOOLCORESIZE_KEY = "threadPoolCoreSize";
    public static final int THREADPOOLCORESIZE_DEF_VAL = 10;

    public static final String THREADPOOLQUEUESIZE_KEY = "threadPoolQueueSize";
    public static final int THREADPOOLQUEUESIZE_DEF_VAL = 5;

}
