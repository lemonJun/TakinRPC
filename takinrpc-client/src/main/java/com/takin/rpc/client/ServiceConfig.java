package com.takin.rpc.client;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * ServiceConfig
 * 
 * @author Service Platform Architecture Team (spat@58.com)
 */
public final class ServiceConfig {

    private String servicename;
    private int serviceid;
    private List<String> address = new ArrayList<String>();
    private long autoShrink = 20;
    private long sendTimeout = 2000;
    private long receiveTimeout = 3000;
    private long waitTimeout = 3000;
    private boolean usezk = false;
    private String zkhosts = "";

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public int getServiceid() {
        return serviceid;
    }

    public void setServiceid(int serviceid) {
        this.serviceid = serviceid;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public long getAutoShrink() {
        return autoShrink;
    }

    public void setAutoShrink(long autoShrink) {
        this.autoShrink = autoShrink;
    }

    public long getSendTimeout() {
        return sendTimeout;
    }

    public void setSendTimeout(long sendTimeout) {
        this.sendTimeout = sendTimeout;
    }

    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public long getWaitTimeout() {
        return waitTimeout;
    }

    public void setWaitTimeout(long waitTimeout) {
        this.waitTimeout = waitTimeout;
    }

    public boolean isUsezk() {
        return usezk;
    }

    public void setUsezk(boolean usezk) {
        this.usezk = usezk;
    }

    public String getZkhosts() {
        return zkhosts;
    }

    public void setZkhosts(String zkhosts) {
        this.zkhosts = zkhosts;
    }

}
