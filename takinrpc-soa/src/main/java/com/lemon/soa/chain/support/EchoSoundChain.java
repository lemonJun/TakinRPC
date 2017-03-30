package com.lemon.soa.chain.support;

import java.util.Map;

import com.lemon.soa.chain.AbstractNeuralChain;
import com.lemon.soa.common.exception.AlarmException;
import com.lemon.soa.common.exception.ProcessorException;
import com.lemon.soa.common.exception.echosound.EchoSoundException;
import com.lemon.soa.common.spi.SPI;
import com.lemon.soa.moduler.ModulerType;
import com.lemon.soa.moduler.extension.echosound.EchoSoundType;
import com.lemon.soa.moduler.extension.echosound.IEchoSoundProcessor;
import com.lemon.soa.moduler.senior.alarm.IAlarmType;
import com.lemon.soa.processor.INeuralProcessor;

/**
 * 回声探测调用链
 * 
 * me
 *
 * @param <REQ>
 * @param <RES>
 */
@SPI(order = 7)
public class EchoSoundChain<REQ, RES> extends AbstractNeuralChain<REQ, RES> {

    @Override
    public RES chain(REQ req, final String neuralId, final EchoSoundType echoSoundType, final Map<String, Object> blackWhiteIdKeyVals, final INeuralProcessor<REQ, RES> processor, Object... args) {

        // $NON-NLS-回声探测开始$
        return moduler.getEchoSound().echosound(echoSoundType, req, new IEchoSoundProcessor<REQ, RES>() {

            @Override
            public RES processor(REQ req, Object... args) throws ProcessorException {
                return neuralChain.chain(req, neuralId, echoSoundType, blackWhiteIdKeyVals, processor, args);
            }

            /**
             * 回声探测请求
             */
            @Override
            public REQ $echo(REQ req, Object... args) throws EchoSoundException {
                return processor.$echo(req, args);
            }

            /**
             * 回声探测响应
             */
            @Override
            public RES $rebound(REQ req, Object... args) throws EchoSoundException {
                return processor.$rebound(req, args);
            }

            /**
             * 告警
             */
            @Override
            public void alarm(IAlarmType alarmType, REQ req, RES res, Throwable t, Object... args) throws AlarmException {
                processor.alarm(ModulerType.EchoSound, alarmType, req, res, t, args);
            }
        });

    }

}