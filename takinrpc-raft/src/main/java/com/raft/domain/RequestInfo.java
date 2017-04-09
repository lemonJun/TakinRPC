package com.raft.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/31.
 */
public class RequestInfo implements Serializable {
    private long requestId;
    private Object service;
    private String addr;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
