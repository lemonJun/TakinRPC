package com.lemon.soa.moduler.extension.degrade;

import com.lemon.soa.common.exception.degrade.DegradeException;
import com.lemon.soa.moduler.senior.alarm.processor.IAlarmTypeProcessor;
import com.lemon.soa.processor.IProcessor;

/**
 * 服务降级处理器
 * 
 * me
 *
 * @param <REQ>
 * @param <RES>
 */
public interface IDegradeProcessor<REQ, RES> extends IProcessor<REQ, RES>, IAlarmTypeProcessor<REQ, RES> {

    /**
     * Mock降级
     * 
     * @param req
     * @param args
     * @return
     * @throws DegradeException
     */
    RES mock(REQ req, Object... args) throws DegradeException;

    /**
     * 业务降级处理器
     * 
     * @param req
     * @param args
     * @return
     * @throws Throwable
     */
    RES bizDegrade(REQ req, Object... args) throws DegradeException;

}
