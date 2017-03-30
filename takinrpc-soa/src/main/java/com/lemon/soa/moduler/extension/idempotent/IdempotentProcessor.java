package com.lemon.soa.moduler.extension.idempotent;

import com.lemon.soa.common.exception.idempotent.IdempotentException;
import com.lemon.soa.moduler.senior.alarm.processor.IAlarmTypeProcessor;
import com.lemon.soa.processor.IProcessor;

/**
 * 幂等处理
 * 
 * me
 * @version v1.0
 */
public interface IdempotentProcessor<REQ, RES> extends IProcessor<REQ, RES>, IAlarmTypeProcessor<REQ, RES> {

    /**
     * 幂等请求校验
     * 
     * @param neuralId
     * @param args
     * @return
     * @throws IdempotentException
     */
    boolean check(String neuralId, Object... args) throws IdempotentException;

    /**
     * 获取幂等数据
     * 
     * @param neuralId
     * @param args
     * @return
     * @throws IdempotentException
     */
    RES get(String neuralId, Object... args) throws IdempotentException;

    /**
     * 幂等结果持久化
     * 
     * @param req
     * @param res
     * @param args
     * @throws IdempotentException
     */
    void storage(REQ req, RES res, Object... args) throws IdempotentException;

}
