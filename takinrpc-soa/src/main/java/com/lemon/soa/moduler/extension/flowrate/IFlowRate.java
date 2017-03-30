package com.lemon.soa.moduler.extension.flowrate;

import com.lemon.soa.common.exception.flowrate.FlowrateException;
import com.lemon.soa.moduler.IModuler;

/**
 * 流量控制
 * 
 * me
 */
public interface IFlowRate<REQ, RES> extends IModuler<REQ, RES> {

    /**
     * 流量控制器
     * <br>
     * 第一步:并发流控<br>
     * 第二步:QPS流控<br>
     * @param req
     * @param processor
     * @param args
     * @return
     * @throws FlowrateException
     */
    RES flowrate(REQ req, IFlowRateProcessor<REQ, RES> processor, Object... args) throws FlowrateException;

}