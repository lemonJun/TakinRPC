package com.lemon.soa.moduler.extension.pipescaling;

public class PipeScalingConf {

    /**
     * 放通率:用于控制后端服务放通的概率,默认1.0表示100%放通,即不做限制
     */
    public static final String SCALING_SWITCH_KEY = "switch";
    public static final boolean SCALING_SWITCH_DEF_KEY = true;

    /**
     * 放通率:用于控制后端服务放通的概率,默认1.0表示100%放通,即不做限制
     */
    public static final String SCALING_RATE_KEY = "scalingRate";
    public static final double SCALING_RATE_DEF_VAL = 1.0000;

}
