package com.lemon.soa.moduler.extension.echosound;

import com.lemon.soa.common.exception.echosound.EchoSoundException;
import com.lemon.soa.moduler.senior.alarm.processor.IAlarmTypeProcessor;
import com.lemon.soa.processor.IProcessor;

/**
 * 回声探测
 * me
 * @version v1.0
 */
public interface IEchoSoundProcessor<REQ, RES> extends IProcessor<REQ, RES>, IAlarmTypeProcessor<REQ, RES> {

    /**
     * 发射探测
     * 
     * @param req
     * @param args
     * @return
     * @throws EchoSoundException
     */
    REQ $echo(REQ req, Object... args) throws EchoSoundException;

    /**
     * 探测反弹
     * 
     * @param req
     * @param args
     * @return
     * @throws EchoSoundException
     */
    RES $rebound(REQ req, Object... args) throws EchoSoundException;

}
