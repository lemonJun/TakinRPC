package com.lemon.soa.moduler.senior.monitor.core;

import java.util.Map;

import com.lemon.soa.common.exception.monitor.MonitorCollectException;
import com.lemon.soa.common.exception.monitor.MonitorLookupException;
import com.lemon.soa.moduler.Moduler;
import com.lemon.soa.moduler.senior.monitor.IMonitor;

public class MonitorFactory<REQ, RES> implements IMonitor<REQ, RES> {

    @Override
    public void notify(Moduler<REQ, RES> moduler) {
        // TODO Auto-generated method stub

    }

    @Override
    public void init() throws Throwable {
        // TODO Auto-generated method stub

    }

    @Override
    public void destory() throws Throwable {
        // TODO Auto-generated method stub

    }

    @Override
    public void collect(Map<String, Object> data, Object... args) throws MonitorCollectException {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<String, Object> lookup(Map<String, Object> data, Object... args) throws MonitorLookupException {
        // TODO Auto-generated method stub
        return null;
    }

}
