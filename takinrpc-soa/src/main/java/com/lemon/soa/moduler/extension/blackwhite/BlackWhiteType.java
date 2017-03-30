package com.lemon.soa.moduler.extension.blackwhite;

public enum BlackWhiteType {

    /**
     * 黑名单
     */
    BLACK("BLACK"),

    /**
     * 白名单
     */
    WHITE("WHITE");

    String val;

    BlackWhiteType(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

}
