package com.raft.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Administrator on 2016/4/9.
 */
public class StandardEventManager implements EventContext {

    private Map<String, EventListenerPool> listenerMap = new ConcurrentHashMap<String, EventListenerPool>();

    private ScheduledExecutorService executorService = null;

    public StandardEventManager(int threadPoolSize) {
        this.executorService = Executors.newScheduledThreadPool(threadPoolSize, new NameThreadFactory("EventThread-"));

    }

    private EventListenerPool getListenerPool(String eventType) {
        EventListenerPool pool = listenerMap.get(eventType);
        if (pool == null) {
            EventListenerPool newPool = new EventListenerPool();
            pool = listenerMap.putIfAbsent(eventType, newPool);

            if (pool == null) {
                pool = newPool;
            }
        }
        return pool;
    }

    public <T> void publicOnceListener(String eventType, EventListener<T> eventListener) {
        if (eventType == null || "".equals(eventType.trim())) {
            return;
        }

        this.getListenerPool(eventType).pushOnceListener(eventListener);
    }

    public <T> void addListener(String eventType, EventListener<T> eventListener) {
        if ((eventType != null && !"".equals(eventType.trim())) && eventListener != null) {
            this.getListenerPool(eventType).addListener(eventListener);
        }
    }

    public <T> void removeListener(String eventType, EventListener<T> eventListener) {
        this.getListenerPool(eventType).removeListener(eventListener);
    }

    public <T> void fireAsyncEvent(final String eventType, final T eventData) {
        fireEvent(eventType, false, eventData);
    }

    public <T> void fireSyncEvent(final String eventType, final T eventData) {
        fireEvent(eventType, true, eventData);
    }

    public <T> void fireEvent(final String eventType, boolean isAsync, final T eventData) {
        if (!isAsync) {
            executeEvent(eventType, eventData);
        } else {
            this.executorService.submit(new Runnable() {
                public void run() {
                    executeEvent(eventType, eventData);
                }
            });
        }
    }

    public <T> void executeEvent(String eventType, T eventData) {
        EventListenerPool listenerPool = this.getListenerPool(eventType);
        if (listenerPool != null) {
            List<EventListener<?>> snapshot = listenerPool.getListenerSnapshot();
            for (EventListener<?> listenerItem : snapshot) {
                try {
                    EventListener<Object> listener = (EventListener<Object>) listenerItem;
                    listener.onEvent(eventType, eventData);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        List<EventListener<?>> onceList = listenerPool.popOnceListener();
        if (onceList != null) {
            for (EventListener<?> listenerItem : onceList) {
                try {
                    EventListener<Object> listener = (EventListener<Object>) listenerItem;
                    listener.onEvent(eventType, eventData);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class EventListenerPool {
    private final Object ONCE_LOCK = new Object();
    private ArrayList<EventListener<?>> onceListener;
    private final CopyOnWriteArrayList<EventListener<?>> listenerList;

    public EventListenerPool() {
        onceListener = new ArrayList<EventListener<?>>();
        listenerList = new CopyOnWriteArrayList<EventListener<?>>();
    }

    public void addListener(EventListener<?> eventListener) {
        listenerList.add(eventListener);
    }

    public void pushOnceListener(EventListener<?> eventListener) {
        synchronized (ONCE_LOCK) {
            onceListener.add(eventListener);
        }
    }

    public List<EventListener<?>> popOnceListener() {
        List<EventListener<?>> onceList = null;
        synchronized (ONCE_LOCK) {
            onceList = this.onceListener;
            this.onceListener = new ArrayList<EventListener<?>>();
        }
        return onceList;
    }

    public List<EventListener<?>> getListenerSnapshot() {
        return new ArrayList<EventListener<?>>(this.listenerList);
    }

    public void removeListener(EventListener<?> eventListener) {
        listenerList.remove(eventListener);
    }
}

class NameThreadFactory implements ThreadFactory {
    private String nameSimple = "Thread-%s";
    private int index = 1;

    public NameThreadFactory(String nameSimple) {
        this.nameSimple = nameSimple;
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(String.format(nameSimple, index++));
        t.setDaemon(true);
        return t;
    }
}