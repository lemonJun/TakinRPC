package com.lemon.soa.moduler.extension.degrade;

import com.lemon.soa.common.exception.degrade.DegradeException;
import com.lemon.soa.moduler.senior.alarm.processor.IAlarmTypeProcessor;

/**
 * 业务服务降级
 * 
 * me
 */
public interface IBizDegradeProcessor<REQ, RES> extends IAlarmTypeProcessor<REQ, RES> {

    /**
     * 业务降级处理器
     * 
     * @param req
     * @param processor
     * @param args
     * @return
     * @throws Throwable
     */
    RES processor(REQ req, IDegradeProcessor<REQ, RES> processor, Object... args) throws DegradeException;

}
