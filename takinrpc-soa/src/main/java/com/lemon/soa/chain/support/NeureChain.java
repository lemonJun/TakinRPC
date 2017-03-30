package com.lemon.soa.chain.support;

import java.util.Map;

import com.lemon.soa.chain.AbstractNeuralChain;
import com.lemon.soa.common.exception.AlarmException;
import com.lemon.soa.common.exception.ProcessorException;
import com.lemon.soa.common.exception.neure.NeureBreathException;
import com.lemon.soa.common.exception.neure.NeureCallbackException;
import com.lemon.soa.common.exception.neure.NeureFaultTolerantException;
import com.lemon.soa.common.spi.SPI;
import com.lemon.soa.moduler.ModulerType;
import com.lemon.soa.moduler.extension.echosound.EchoSoundType;
import com.lemon.soa.moduler.neure.INeureProcessor;
import com.lemon.soa.moduler.senior.alarm.IAlarmType;
import com.lemon.soa.processor.INeuralProcessor;

/**
 * 容错调用链
 * 
 * me
 *
 * @param <REQ>
 * @param <RES>
 */
@SPI(order = 8)
public class NeureChain<REQ, RES> extends AbstractNeuralChain<REQ, RES> {

    @Override
    public RES chain(REQ req, final String neuralId, final EchoSoundType echoSoundType, final Map<String, Object> blackWhiteIdKeyVals, final INeuralProcessor<REQ, RES> processor, Object... args) {

        // $NON-NLS-容错内核开始(熔断拒绝→超时控制→舱壁隔离→服务容错→慢性尝试)$
        return moduler.getNeure().neure(req, new INeureProcessor<REQ, RES>() {

            @Override
            public RES processor(REQ req, Object... args) throws ProcessorException {// 内核业务封装
                return processor.processor(req, args);
            }

            /**
             * 内核容错
             */
            @Override
            public RES faulttolerant(REQ req, Object... args) throws NeureFaultTolerantException {
                return processor.faulttolerant(req, args);
            }

            /**
             * 内核呼吸
             */
            @Override
            public long breath(long nowTimes, long nowExpend, long maxRetryNum, Object... args) throws NeureBreathException {
                return processor.breath(nowTimes, nowExpend, maxRetryNum, args);
            }

            /**
             * 内核回调
             */
            @Override
            public void callback(RES res, Object... args) throws NeureCallbackException {
                processor.callback(res, args);
            }

            /**
             * 告警
             */
            @Override
            public void alarm(IAlarmType alarmType, REQ req, RES res, Throwable t, Object... args) throws AlarmException {
                processor.alarm(ModulerType.Neure, alarmType, req, res, t, args);
            }
        }, args);// $NON-NLS-容错内核结束$

    }

}