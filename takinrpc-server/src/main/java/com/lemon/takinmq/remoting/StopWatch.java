package com.lemon.takinmq.remoting;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StopWatch {

    private Date monitorTime;

    private String methodName;

    private String lookup;

    private String fromIP;
    private String localIP;

    private Map<String, PerformanceCounter> mapCounter = new HashMap<String, PerformanceCounter>();

    public void start() {
        mapCounter.put("default", new PerformanceCounter("", "", System.currentTimeMillis()));
    }

    public void stop() {
        PerformanceCounter pc = mapCounter.get("default");
        if (pc != null) {
            pc.setEndTime(System.currentTimeMillis());
        }
    }

    public void reset() {
        mapCounter.clear();
    }

    public void startNew(String key, String description) {
        mapCounter.put(key, new PerformanceCounter(key, description, System.currentTimeMillis()));
    }

    public void stop(String key) {
        PerformanceCounter pc = mapCounter.get(key);
        if (pc != null) {
            pc.setEndTime(System.currentTimeMillis());
        }
    }

    public class PerformanceCounter {

        private String key;
        private String description;
        private long startTime;
        private long endTime;

        public PerformanceCounter() {

        }

        public PerformanceCounter(String key, String description, long startTime) {
            this.setKey(key);
            this.setDescription(description);
            this.setStartTime(startTime);
        }

        public long getExecuteTime() {
            return endTime - startTime;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
    }

    public Map<String, PerformanceCounter> getMapCounter() {
        return mapCounter;
    }

    public Date getMonitorTime() {
        return monitorTime;
    }

    public void setMonitorTime(Date monitorTime) {
        this.monitorTime = monitorTime;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public String getLookup() {
        return lookup;
    }

    public String getFromIP() {
        return fromIP;
    }

    public void setFromIP(String fromIP) {
        this.fromIP = fromIP;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }
}