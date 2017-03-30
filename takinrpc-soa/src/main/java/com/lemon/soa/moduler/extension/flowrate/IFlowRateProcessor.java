package com.lemon.soa.moduler.extension.flowrate;

import com.lemon.soa.moduler.senior.alarm.processor.IAlarmTypeProcessor;
import com.lemon.soa.processor.IProcessor;

public interface IFlowRateProcessor<REQ, RES> extends IProcessor<REQ, RES>, IAlarmTypeProcessor<REQ, RES> {

}