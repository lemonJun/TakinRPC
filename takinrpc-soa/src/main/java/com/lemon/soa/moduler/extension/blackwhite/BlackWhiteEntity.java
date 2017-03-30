package com.lemon.soa.moduler.extension.blackwhite;

import java.util.ArrayList;
import java.util.List;

/**
 * The Black White List Entity.
 * 
 * me
 */
public class BlackWhiteEntity {

    private long time = System.currentTimeMillis();
    private boolean blackEnabled = false;
    private boolean whiteEnabled = false;
    //在线数据
    private List<String> onlineBlackData = new ArrayList<String>();
    private List<String> onlineWhiteData = new ArrayList<String>();
    //离线数据
    private List<String> offlineBlackData = new ArrayList<String>();
    private List<String> offlineWhiteData = new ArrayList<String>();

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isBlackEnabled() {
        return blackEnabled;
    }

    public void setBlackEnabled(boolean blackEnabled) {
        this.blackEnabled = blackEnabled;
    }

    public boolean isWhiteEnabled() {
        return whiteEnabled;
    }

    public void setWhiteEnabled(boolean whiteEnabled) {
        this.whiteEnabled = whiteEnabled;
    }

    public List<String> getOnlineBlackData() {
        return onlineBlackData;
    }

    public void setOnlineBlackData(List<String> onlineBlackData) {
        this.onlineBlackData = onlineBlackData;
    }

    public List<String> getOnlineWhiteData() {
        return onlineWhiteData;
    }

    public void setOnlineWhiteData(List<String> onlineWhiteData) {
        this.onlineWhiteData = onlineWhiteData;
    }

    public List<String> getOfflineBlackData() {
        return offlineBlackData;
    }

    public void setOfflineBlackData(List<String> offlineBlackData) {
        this.offlineBlackData = offlineBlackData;
    }

    public List<String> getOfflineWhiteData() {
        return offlineWhiteData;
    }

    public void setOfflineWhiteData(List<String> offlineWhiteData) {
        this.offlineWhiteData = offlineWhiteData;
    }

    @Override
    public String toString() {
        return "BlackWhiteEntity [time=" + time + ", blackEnabled=" + blackEnabled + ", whiteEnabled=" + whiteEnabled + ", onlineBlackData=" + onlineBlackData + ", onlineWhiteData=" + onlineWhiteData + ", offlineBlackData=" + offlineBlackData + ", offlineWhiteData=" + offlineWhiteData + "]";
    }

}
