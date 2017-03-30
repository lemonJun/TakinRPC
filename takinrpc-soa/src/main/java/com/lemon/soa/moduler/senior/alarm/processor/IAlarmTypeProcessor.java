package com.lemon.soa.moduler.senior.alarm.processor;

import com.lemon.soa.common.exception.AlarmException;
import com.lemon.soa.moduler.senior.alarm.IAlarmType;

/**
 * 告警中心
 * 
 * me
 * @version v1.0
 */
public interface IAlarmTypeProcessor<REQ, RES> {

    /**
     * 告警处理
     * 
     * @param alarmType 告警类型
     * @param req 请求对象
     * @param res 响应对象
     * @param t 告警异常
     * @param args 其他参数
     * @throws AlarmException
     */
    void alarm(IAlarmType alarmType, REQ req, RES res, Throwable t, Object... args) throws AlarmException;

}
