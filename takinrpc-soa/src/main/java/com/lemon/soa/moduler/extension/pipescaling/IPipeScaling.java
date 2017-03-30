package com.lemon.soa.moduler.extension.pipescaling;

import com.lemon.soa.common.exception.pipescaling.PipeScalingException;
import com.lemon.soa.moduler.IModuler;

/**
 * 管道缩放
 * 
 * me
 * @version v1.0
 */
public interface IPipeScaling<REQ, RES> extends IModuler<REQ, RES> {

    /**
     * 
     * @param req
     * @param processor
     * @param args
     * @return
     * @throws PipeScalingException
     */
    RES pipescaling(REQ req, IPipeScalingProcessor<REQ, RES> processor, Object... args) throws PipeScalingException;

}
