package com.lemon.soa.moduler.extension.gracestop;

import com.lemon.soa.type.ITypeAdaptor;

public enum GraceStopStatusType implements ITypeAdaptor {

    /**
     * 在线
     */
    ONLINE("ONLINE", "This is online."),

    /**
     * 离线
     */
    OFFLINE("OFFLINE", "This is offline.");

    String val;
    String msg;

    GraceStopStatusType(String val, String msg) {
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
