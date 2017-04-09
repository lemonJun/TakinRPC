package com.raft.event;

/**
 * Created by Administrator on 2016/4/9.
 */
public interface EventContext {
    <T> void publicOnceListener(String eventType, EventListener<T> eventListener);

    <T> void addListener(String eventType, EventListener<T> eventListener);

    <T> void removeListener(String eventType, EventListener<T> eventListener);

    <T> void fireAsyncEvent(String eventType, T eventData);

}
