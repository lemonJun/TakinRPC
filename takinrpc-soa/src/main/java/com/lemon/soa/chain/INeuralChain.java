package com.lemon.soa.chain;

import java.util.Map;

import com.lemon.soa.moduler.Moduler;
import com.lemon.soa.moduler.extension.echosound.EchoSoundType;
import com.lemon.soa.processor.INeuralProcessor;

/**
 * 微服务神经元调用链
 * 
 * me
 *
 * @param <REQ>
 * @param <RES>
 */
public interface INeuralChain<REQ, RES> {

    /**
     * 设置调用链
     * @param moduler
     * @param neuralChain
     */
    void initChain(Moduler<REQ, RES> moduler, INeuralChain<REQ, RES> neuralChain);

    /**
     * 调用链
     * 
     * @param req
     * @param neuralId
     * @param echoSoundType
     * @param blackWhiteIdKeyVals
     * @param processor
     * @param args
     * @return
     */
    RES chain(REQ req, String neuralId, EchoSoundType echoSoundType, Map<String, Object> blackWhiteIdKeyVals, INeuralProcessor<REQ, RES> processor, Object... args);
    
}
