package com.lemon.soa.moduler.extension.idempotent;

public class IdempotentConf {

    /**
     * 幂等总开关
     */
    public static final String IDEMPOTENT_SWITCH_KEY = "switch";
    public static final boolean IDEMPOTENT_SWITCH_DEF_VAL = false;

    /**
     * 持久化开关
     */
    public static final String STORAGE_SWITCH_KEY = "storageSwitch";
    public static final boolean STORAGE_SWITCH_DEF_VAL = false;

    /**
     * 如果资源已存在是否抛异常进行处理,true则抛异常,false则不抛异常而获取结果
     */
    public static final String EXCEPTION_RES_SWITCH_KEY = "exceptionSwitch";
    public static final boolean EXCEPTION_RES_SWITCH_DEF_VAL = false;
}
