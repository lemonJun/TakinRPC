package com.lemon.soa.moduler.extension.degrade;

public class DegradeConf {

    public static final String SWITCH_KEY = "switch";
    public static final boolean SWITCH_DEF_VALUE = false;

    public static final String DEGRADETYPE_KEY = "degradetype";
    public static final String DEGRADETYPE_DEF_VALUE = DegradeType.FAULTTOLERANT.toString();

    public static final String STRATEGYTYPE_KEY = "strategytype";
    public static final String STRATEGYTYPE_DEF_VALUE = StrategyType.EXCEPTION.toString();

}
