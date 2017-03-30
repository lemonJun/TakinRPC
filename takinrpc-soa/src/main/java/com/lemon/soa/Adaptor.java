package com.lemon.soa;

/**
 * Adapter for each module
 * 
 * me
 */
public interface Adaptor {

    /**
     * Module initialization
     * 
     * @throws Throwable
     */
    void init() throws Throwable;

    /**
     * Module destruction
     * 
     * @throws Throwable
     */
    void destory() throws Throwable;

}
