package com.raft.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/31.
 */
public class VoteReq implements Serializable {
    private String serviceId;
    private String term;
    private String remoteAddress;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
