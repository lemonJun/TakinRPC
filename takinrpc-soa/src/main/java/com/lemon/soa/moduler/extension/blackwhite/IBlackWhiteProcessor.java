package com.lemon.soa.moduler.extension.blackwhite;

import com.lemon.soa.moduler.senior.alarm.processor.IAlarmTypeProcessor;
import com.lemon.soa.processor.IProcessor;

public interface IBlackWhiteProcessor<REQ, RES> extends IProcessor<REQ, RES>, IAlarmTypeProcessor<REQ, RES> {

}
