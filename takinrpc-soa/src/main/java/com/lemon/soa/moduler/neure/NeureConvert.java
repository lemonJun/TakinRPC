package com.lemon.soa.moduler.neure;

import com.lemon.soa.Conf;
import com.lemon.soa.common.URL;

/**
 * 神经转换
 * 
 * me
 * @version v1.0
 */
public class NeureConvert {

    /**
     * 配置转换
     * 
     * @param url
     * @return
     */
    public static NeureEntity convert(URL url) {
        NeureEntity entity = new NeureEntity();
        //神经内核开关
        entity.setNeureSwitch(url.getModulerParameter(Conf.NEURE, NeureConf.SWITCH_KEY, NeureConf.SWITCH_DEF_VAL));
        //容错开关
        entity.setFallback(url.getModulerParameter(Conf.NEURE, NeureConf.FALLBACK_SWITCH_KEY, NeureConf.FALLBACK_SWITCH_DEF_VAL));
        //线程之间参数传递
        entity.setThreadContext(url.getModulerParameter(Conf.NEURE, NeureConf.THREADCONTEXT_SWITCH_KEY, NeureConf.THREADCONTEXT_SWITCH_DEF_VAL));
        //最大重试次数
        entity.setMaxRetryTimes(url.getModulerParameter(Conf.NEURE, NeureConf.MAX_RETRYTIMES_KEY, NeureConf.MAX_RETRYTIMES_DEF_VAL));

        HystrixSetter hystrixSetter = new HystrixSetter();
        //执行隔离线程超时毫秒,默认为1000ms
        hystrixSetter.setEitTimeout(url.getModulerParameter(Conf.NEURE, NeureConf.EITTIMEOUT_KEY, NeureConf.EITTIMEOUT_DEF_VAL));
        //执行超时时间,默认为1000ms
        hystrixSetter.setEtimeout(url.getModulerParameter(Conf.NEURE, NeureConf.ETIMEOUT_KEY, NeureConf.ETIMEOUT_DEF_VAL));
        //调用线程允许请求HystrixCommand.GetFallback()的最大数量，默认10。超出时将会有异常抛出，注意：该项配置对于THREAD隔离模式也起作用
        hystrixSetter.setFismRequests(url.getModulerParameter(Conf.NEURE, NeureConf.FISMREQUESTS_KEY, NeureConf.FISMREQUESTS_DEF_VAL));

        //出错百分比阈值,当达到此阈值后,开始短路,默认50%
        hystrixSetter.setCbErrorRate(url.getModulerParameter(Conf.NEURE, NeureConf.CBERRORRATE_KEY, NeureConf.CBERRORRATE_DEF_VAL));
        //当在配置时间窗口内达到此数量的失败后,进行短路,默认20个
        hystrixSetter.setCbRequests(url.getModulerParameter(Conf.NEURE, NeureConf.CBREQUESTS_KEY, NeureConf.CBREQUESTS_DEF_VAL));
        //短路多久以后开始尝试是否恢复,默认5s
        hystrixSetter.setCbSleepWindow(url.getModulerParameter(Conf.NEURE, NeureConf.CBSLEEPWINDOW_KEY, NeureConf.CBSLEEPWINDOW_DEF_VAL));

        //线程池设置:线程池核心线程数,默认为10
        hystrixSetter.setThreadPoolCoreSize(url.getModulerParameter(Conf.NEURE, NeureConf.THREADPOOLCORESIZE_KEY, NeureConf.THREADPOOLCORESIZE_DEF_VAL));
        //排队线程数量阈值，默认为5，达到时拒绝，如果配置了该选项，队列的大小是该队列
        hystrixSetter.setThreadPoolQueueSize(url.getModulerParameter(Conf.NEURE, NeureConf.THREADPOOLQUEUESIZE_KEY, NeureConf.THREADPOOLQUEUESIZE_DEF_VAL));

        entity.setHystrixSetter(hystrixSetter);

        return entity;
    }

}
