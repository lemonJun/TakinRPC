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
    private ServerProfile profile;
    private List<String> address = new ArrayList<String>();

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

    public ServerProfile getProfile() {
        return profile;
    }

    public void setProfile(ServerProfile profile) {
        this.profile = profile;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

}
