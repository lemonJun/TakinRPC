package com.lemon.soa.chain;

import com.lemon.soa.moduler.Moduler;

/**
 * 微服务神经元抽象类调用链
 * 
 * me
 *
 * @param <REQ>
 * @param <RES>
 */
public abstract class AbstractNeuralChain<REQ, RES> implements INeuralChain<REQ, RES> {

    public Moduler<REQ, RES> moduler;
    public INeuralChain<REQ, RES> neuralChain;

    @Override
    public void initChain(Moduler<REQ, RES> moduler, INeuralChain<REQ, RES> neuralChain) {
        this.moduler = moduler;
        this.neuralChain = neuralChain;
    }

}