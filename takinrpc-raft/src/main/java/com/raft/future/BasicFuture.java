package com.raft.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2016/10/31.
 */
public class BasicFuture<T> implements Future<T> {
    private volatile boolean completed;
    private volatile boolean cancelled;
    private volatile Throwable ex;
    private volatile T result;
    private final FutureCallback<T> callback;

    public BasicFuture() {
        this.callback = null;
    }

    public BasicFuture(FutureCallback<T> futureCallback) {
        this.callback = futureCallback;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this) {
            if (this.isDone()) {
                return false;
            }

            this.completed = true;
            this.cancelled = true;
            this.notifyAll();
        }

        if (this.callback != null) {
            this.callback.cancelled();
        }

        return true;
    }

    public boolean completed(T result) {
        synchronized (this) {
            if (this.isDone()) {
                return false;
            }

            this.completed = true;
            this.result = result;
            this.notifyAll();
        }

        if (this.callback != null) {
            this.callback.completed(result);
        }

        return true;
    }

    public boolean failed(Throwable exception) {
        synchronized (this) {
            if (this.isDone()) {
                return false;
            }

            this.completed = true;
            this.ex = exception;
            this.notifyAll();
        }

        if (this.callback != null) {
            this.callback.failed(exception);
        }
        return true;
    }

    public boolean cancel() {
        return this.cancel(true);
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public boolean isDone() {
        return this.completed;
    }

    public synchronized T get() throws InterruptedException, ExecutionException {
        while (!this.isDone()) {
            this.wait();
        }

        return this.getResult();
    }

    public T getResult() throws ExecutionException {
        if (this.ex != null) {
            if (this.ex instanceof ExecutionException) {
                throw (ExecutionException) this.ex;
            } else if (this.ex instanceof RuntimeException) {
                throw (RuntimeException) this.ex;
            } else {
                throw new ExecutionException(this.ex.getMessage(), this.ex);
            }
        } else {
            return this.result;
        }
    }

    public synchronized T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long msecs = unit.toMillis(timeout);
        long startTime = msecs <= 0L ? 0L : System.currentTimeMillis();
        long waitTime = msecs;
        if (this.isDone()) {
            return this.result;
        } else if (msecs <= 0L) {
            throw new IllegalArgumentException();
        } else {
            do {
                this.wait(waitTime);
                if (this.isDone()) {
                    return this.getResult();
                }

                waitTime = msecs - (System.currentTimeMillis() - startTime);
            } while (waitTime > 0L);

            throw new TimeoutException();
        }
    }
}
