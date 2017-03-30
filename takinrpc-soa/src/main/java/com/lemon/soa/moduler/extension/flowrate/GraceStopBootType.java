package com.lemon.soa.moduler.extension.flowrate;

import com.lemon.soa.type.ITypeAdaptor;

public enum GraceStopBootType implements ITypeAdaptor {

    /**
     * 开机
     */
    BOOT("boot", "This is boot."),

    /**
     * 停机
     */
    STOP("stop", "This is stop.");

    String val;
    String msg;

    GraceStopBootType(String val, String msg) {
        this.val = val;
        this.msg = msg;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
