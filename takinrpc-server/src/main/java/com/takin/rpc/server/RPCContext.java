package com.takin.rpc.server;

import java.util.HashSet;
import java.util.Set;

public class RPCContext {
    private String rpcName;
    private Set<Class<?>> classSets = new HashSet<Class<?>>();
    private String configPath = null;
    
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

}
