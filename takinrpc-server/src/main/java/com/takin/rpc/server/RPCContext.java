package com.takin.rpc.server;

import java.util.HashSet;
import java.util.Set;

public class RPCContext {
    private String rpcName = "test";
    private Set<Class<?>> classSets = new HashSet<Class<?>>();
    private String configPath = null;
    private String servicePath = null;
    private String libPath = null;

    public String getRpcName() {
        return rpcName;
    }

    public void setRpcName(String rpcName) {
        this.rpcName = rpcName;
    }
    
    public Set<Class<?>> getClassSets() {
        return classSets;
    }

    public void setClassSets(Set<Class<?>> classSets) {
        this.classSets = classSets;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getServicePath() {
        return servicePath;
    }
    
    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    public String getLibPath() {
        return libPath;
    }

    public void setLibPath(String libPath) {
        this.libPath = libPath;
    }

    @Override
    public String toString() {
        return "RPCContext [rpcName=" + rpcName + ", classSets=" + classSets + ", configPath=" + configPath + ", servicePath=" + servicePath + "]";
    }

}
