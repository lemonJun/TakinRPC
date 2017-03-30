package com.lemon.soa.chain.support;

import java.util.Map;

import com.lemon.soa.chain.AbstractNeuralChain;
import com.lemon.soa.common.exception.AlarmException;
import com.lemon.soa.common.exception.ProcessorException;
import com.lemon.soa.common.exception.degrade.DegradeException;
import com.lemon.soa.common.spi.SPI;
import com.lemon.soa.moduler.ModulerType;
import com.lemon.soa.moduler.extension.degrade.IDegradeProcessor;
import com.lemon.soa.moduler.extension.echosound.EchoSoundType;
import com.lemon.soa.moduler.senior.alarm.IAlarmType;
import com.lemon.soa.processor.INeuralProcessor;

/**
 * 服务降级调用链
 * 
 * me
 *
 * @param <REQ>
 * @param <RES>
 */
@SPI(order = 5)
public class DegradeChain<REQ, RES> extends AbstractNeuralChain<REQ, RES> {

    @Override
    public RES chain(REQ req, final String neuralId, final EchoSoundType echoSoundType, final Map<String, Object> blackWhiteIdKeyVals, final INeuralProcessor<REQ, RES> processor, Object... args) {

        // $NON-NLS-服务降级开始$
        return moduler.getDegrade().degrade(req, new IDegradeProcessor<REQ, RES>() {

            @Override
            public RES processor(REQ req, Object... args) throws ProcessorException {
                return neuralChain.chain(req, neuralId, echoSoundType, blackWhiteIdKeyVals, processor, args);
            }

            /**
             * 降级mock
             */
            @Override
            public RES mock(REQ req, Object... args) throws DegradeException {
                return processor.mock(req, args);
            }

            /**
             * 业务降级
             */
            @Override
            public RES bizDegrade(REQ req, Object... args) throws DegradeException {
                return processor.bizDegrade(req, args);
            }

            /**
             * 告警
             */
            @Override
            public void alarm(IAlarmType alarmType, REQ req, RES res, Throwable t, Object... args) throws AlarmException {
                processor.alarm(ModulerType.Degrade, alarmType, req, res, t, args);
            }
        });

    }

}