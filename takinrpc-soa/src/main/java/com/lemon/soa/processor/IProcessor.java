package com.lemon.soa.processor;

import com.lemon.soa.common.exception.ProcessorException;

/**
 * The Moduler Processor.
 * 
 * me
 * @version v1.0
 */
public interface IProcessor<REQ, RES> {

    /**
     * The processor center
     * 
     * @param req
     * @param args
     * @return
     * @throws ProcessorException
     */
    RES processor(REQ req, Object... args) throws ProcessorException;

}
