package com.raft.future;

/**
 * Created by Administrator on 2016/10/31.
 */
public interface FutureCallback<T> {
    void completed(T t);

    void failed(Throwable th);

    void cancelled();
}
