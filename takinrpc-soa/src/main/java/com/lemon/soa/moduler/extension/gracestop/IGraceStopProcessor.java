package com.lemon.soa.moduler.extension.gracestop;

import com.lemon.soa.moduler.senior.alarm.processor.IAlarmTypeProcessor;
import com.lemon.soa.processor.IProcessor;

/**
 * 优雅停机处理器
 * 
 * me
 *
 * @param <REQ>
 * @param <RES>
 */
public interface IGraceStopProcessor<REQ, RES> extends IProcessor<REQ, RES>, IAlarmTypeProcessor<REQ, RES> {

}
