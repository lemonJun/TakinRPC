package com.lemon.soa.processor;

import com.lemon.soa.common.exception.degrade.DegradeException;
import com.lemon.soa.common.exception.echosound.EchoSoundException;
import com.lemon.soa.common.exception.echosound.EchoSoundReboundException;
import com.lemon.soa.common.exception.idempotent.IdempotentException;
import com.lemon.soa.common.exception.neure.NeureBreathException;
import com.lemon.soa.common.exception.neure.NeureCallbackException;
import com.lemon.soa.common.exception.neure.NeureFaultTolerantException;
import com.lemon.soa.moduler.senior.alarm.processor.IAlarmProcessor;

/**
 * 微服务神经元处理中心
 * 
 * me
 * @version v1.0
 */
public interface INeuralProcessor<REQ, RES> extends IProcessor<REQ, RES>, IAlarmProcessor<REQ, RES> {

    //$NON-NLS-容错内核$
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

    //$NON-NLS-回声探测$
    /**
     * 发起探测
     * 
     * @param req
     * @param args
     * @return
     */
    REQ $echo(REQ req, Object... args) throws EchoSoundException;

    /**
     * 反弹探测
     * 
     * @param req
     * @param args
     * @return
     */
    RES $rebound(REQ req, Object... args) throws EchoSoundReboundException;
    
    //$NON-NLS-幂等$
    /**
     * 幂等请求校验(判断是否是幂等请求)
     * 
     * @param neuralId
     * @param args
     * @return
     */
    boolean check(String neuralId, Object... args) throws IdempotentException;

    /**
     * 获取幂等结果
     * 
     * @param neuralId
     * @param args
     * @return
     */
    RES get(String neuralId, Object... args) throws IdempotentException;

    /**
     * 幂等结果持久化
     * 
     * @param req
     * @param res
     * @param args
     */
    void storage(REQ req, RES res, Object... args) throws IdempotentException;

    //$NON-NLS-服务降级$
    /**
     * Mock服务降级
     * 
     * @param req
     * @param args
     * @return
     */
    RES mock(REQ req, Object... args) throws DegradeException;

    /**
     * 业务降级
     * 
     * @param req
     * @param args
     * @return
     */
    RES bizDegrade(REQ req, Object... args) throws DegradeException;

}
