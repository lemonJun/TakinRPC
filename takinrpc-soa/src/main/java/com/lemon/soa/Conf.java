package com.lemon.soa;

/**
 * 公共配置模块
 * 
 * me
 * @version v1.0
 */
public class Conf {

    /**
     * 数据分隔符
     */
    public static final String KV_SEQ = "=>";//KEY=>VALUE分隔符
    public static final String VALS_SEQ = "|";//多个VALUE的分隔符
    public static final String PARAMS_SEQ = ",";//多个参数的分隔符

    /**
     * 优雅停机模块
     */
    public static final String GRACESTOP = "gracestop";

    /**
     * 黑白名单模块
     */
    public static final String BLACKWHITE = "blackwhite";

    /**
     * 管道缩放模块
     */
    public static final String PIPESCALING = "pipescaling";

    /**
     * 流控模块
     */
    public static final String FLOWRATE = "flowrate";

    /**
     * 服务降级模块
     */
    public static final String DEGRADE = "degrade";

    /**
     * 幂等模块
     */
    public static final String IDEMPOTENT = "idempotent";

    /**
     * 回声探测模块
     */
    public static final String ECHOSOUND = "echosound";

    /**
     * 微服务神经元模块
     */
    public static final String NEURE = "neure";

}
