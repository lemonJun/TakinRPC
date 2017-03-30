package com.lemon.soa.moduler.neure;

import com.lemon.soa.common.exception.neure.NeureBreathException;
import com.lemon.soa.common.exception.neure.NeureCallbackException;
import com.lemon.soa.common.exception.neure.NeureFaultTolerantException;
import com.lemon.soa.moduler.senior.alarm.processor.IAlarmTypeProcessor;
import com.lemon.soa.processor.IProcessor;

/**
 * 处理器
 * 
 * me
 * @version v1.0
 */
public interface INeureProcessor<REQ, RES> extends IProcessor<REQ, RES>, IAlarmTypeProcessor<REQ, RES> {

    /**
     * 失败容错
     * 
     * @param req
     * @param args
     * @return
     */
    RES faulttolerant(REQ req, Object... args) throws NeureFaultTolerantException;

    /**
     * 慢性尝试周期计算
     * <br>
     * 1.用于释放句柄资源<br>
     * 2.用于节约资源开销<br>
     * @param nowTimes
     * @param nowExpend
     * @param maxRetryNum
     * @param args
     * @return
     * @throws Throwable
     */
    long breath(long nowTimes, long nowExpend, long maxRetryNum, Object... args) throws NeureBreathException;

    /**
     * 回调服务
     * 
     * @param res
     * @param args
     * @throws Throwable
     */
    void callback(RES res, Object... args) throws NeureCallbackException;

}
