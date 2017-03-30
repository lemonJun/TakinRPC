package com.lemon.soa.moduler.extension.slow;

/**
 * 对数函数
 * y=log(a)x
 * 
 * me
 */
public class LogarithmSlow implements ISlow<Double, Double> {

    public Double function(Double sys, Double x) {
        return Math.log(x) / Math.log(sys);
    }

    @Override
    public void init() throws Throwable {
        // TODO Auto-generated method stub

    }

    @Override
    public void destory() throws Throwable {
        // TODO Auto-generated method stub

    }

    @Override
    public <SYS> Double function(SYS sys, Double x) {
        // TODO Auto-generated method stub
        return null;
    }

}
