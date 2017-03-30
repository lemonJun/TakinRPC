package com.lemon.soa.moduler.extension.flowrate;

import com.lemon.soa.type.ITypeAdaptor;

/**
 * The flow rate type.
 * 
 * me
 */
public enum FlowRateType implements ITypeAdaptor {

    /**
     * The rate limiter.
     */
    QPS("QPS", "The rate limiter."),

    /**
     * The concurrent number.
     */
    CCT("CCT", "The concurrent number.");

    String val;
    String msg;

    FlowRateType(String val, String msg) {
        this.val = val;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

}
