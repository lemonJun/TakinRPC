package com.lemon.soa.chain.support;

import java.util.Map;

import com.lemon.soa.chain.AbstractNeuralChain;
import com.lemon.soa.common.exception.AlarmException;
import com.lemon.soa.common.exception.ProcessorException;
import com.lemon.soa.common.exception.idempotent.IdempotentException;
import com.lemon.soa.common.spi.SPI;
import com.lemon.soa.moduler.ModulerType;
import com.lemon.soa.moduler.extension.echosound.EchoSoundType;
import com.lemon.soa.moduler.extension.idempotent.IdempotentProcessor;
import com.lemon.soa.moduler.senior.alarm.IAlarmType;
import com.lemon.soa.processor.INeuralProcessor;

/**
 * 幂等调用链
 * 
 * me
 *
 * @param <REQ>
 * @param <RES>
 */
@SPI(order = 6)
public class IdempotentChain<REQ, RES> extends AbstractNeuralChain<REQ, RES> {

    @Override
    public RES chain(REQ req, final String neuralId, final EchoSoundType echoSoundType, final Map<String, Object> blackWhiteIdKeyVals, final INeuralProcessor<REQ, RES> processor, Object... args) {

        // $NON-NLS-幂等开始$
        return moduler.getIdempotent().idempotent(neuralId, req, new IdempotentProcessor<REQ, RES>() {

            @Override
            public RES processor(REQ req, Object... args) throws ProcessorException {
                return neuralChain.chain(req, neuralId, echoSoundType, blackWhiteIdKeyVals, processor, args);
            }

            /**
             * 幂等请求校验
             */
            @Override
            public boolean check(String neuralId, Object... args) throws IdempotentException {
                return processor.check(neuralId);
            }

            /**
             * 获取幂等数据
             */
            @Override
            public RES get(String neuralId, Object... args) throws IdempotentException {
                return processor.get(neuralId);
            }

            /**
             * 幂等持久化数据
             */
            @Override
            public void storage(REQ req, RES res, Object... args) throws IdempotentException {
                processor.storage(req, res, args);
            }

            /**
             * 告警
             */
            @Override
            public void alarm(IAlarmType alarmType, REQ req, RES res, Throwable t, Object... args) throws AlarmException {
                processor.alarm(ModulerType.Idempotent, alarmType, req, res, t, args);
            }
        });

    }

}