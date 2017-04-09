package com.raft.event;

/**
 * Created by Administrator on 2016/4/9.
 */
public interface EventListener<T> extends java.util.EventListener {
    void onEvent(String event, T eventData) throws Throwable;
}
