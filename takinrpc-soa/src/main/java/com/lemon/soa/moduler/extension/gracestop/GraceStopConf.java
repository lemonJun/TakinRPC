package com.lemon.soa.moduler.extension.gracestop;

import com.lemon.soa.Conf;
import com.lemon.soa.moduler.extension.flowrate.GraceStopBootType;

public class GraceStopConf extends Conf {

    //$NON-NLS-开机开关$
    /**
     * 优雅停机开关KEY
     */
    public static final String SWITCH_KEY = "switch";
    /**
     * 优雅停机开关默认值
     */
    public static final GraceStopBootType SWITCH_DEFAULT_VALUE = GraceStopBootType.BOOT;

    //$NON-NLS-状态$
    /**
     * 开机状态KEY
     */
    public static final String STATUS_KEY = "status";
    /**
     * 开机状态默认值
     */
    public static final GraceStopStatusType STATUS_DEFAULT_VALUE = GraceStopStatusType.ONLINE;

    //$NON-NLS-停机超时$
    /**
     * 关机超时时间KEY
     */
    public static final String SHUTDOWN_TIMEOUT_KEY = "shutdownTimeout";
    /**
     * 关机超时时间默认值,单位毫秒
     */
    public static final long SHUTDOWN_TIMEOUT_DEFAULT_VALUE = 60000;

    //$NON-NLS-开/停机时间$
    /**
     * 开机时间KEY
     */
    public static final String STARTUP_TIME_KEY = "startupTime";
    /**
     * 停机时间KEY
     */
    public static final String SHUTDOWN_TIME_KEY = "shutdownTime";

}
