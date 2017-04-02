package com.takin.rpc.remoting.netty5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.takin.emmet.concurrent.SemaphoreOnce;
import com.takin.rpc.remoting.InvokeCallback;

/**
 * 异步请求应答封装
 */
public class ResponseFuture {

    private static final Logger logger = LoggerFactory.getLogger(ResponseFuture.class);
    private final long opaque;
    private final long timeoutMillis;
    private final long beginTimestamp = System.currentTimeMillis();
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    // 保证信号量至多至少只被释放一次
    // 保证回调的callback方法至多至少只被执行一次
    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);
    private volatile boolean sendRequestOK = true;
    private volatile RemotingProtocol<?> message;
    private volatile Throwable cause;

    private InvokeCallback invokeCallback;

    private SemaphoreOnce once;

    public ResponseFuture(long opaque, long timeoutMillis) {
        this.opaque = opaque;
        this.timeoutMillis = timeoutMillis;
    }

    public ResponseFuture(long opaque, long timeoutMillis, InvokeCallback invokeCallback, SemaphoreOnce once) {
        this.opaque = opaque;
        this.timeoutMillis = timeoutMillis;
        this.invokeCallback = invokeCallback;
        this.once = once;
    }

    public void executeInvokeCallback() {
        if (invokeCallback != null) {
            if (this.executeCallbackOnlyOnce.compareAndSet(false, true)) {
                invokeCallback.operationComplete(this);
            }
        }
    }

    public void release() {
        if (this.once != null) {
            this.once.release();
        }
    }

    public boolean isTimeout() {
        long diff = System.currentTimeMillis() - this.beginTimestamp;
        return diff > this.timeoutMillis;
    }

    //为什么这一步操作这么耗时???
    public RemotingProtocol<?> waitResponse() throws InterruptedException {
        Stopwatch watch = Stopwatch.createStarted();
        logger.debug(String.format("start wait use:%s", watch.toString()));
        boolean retval = countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        logger.debug(String.format("finsh wait use:%s", watch.toString()));
        
        return this.message;
    }

    public void putResponse(final RemotingProtocol message) {
        this.message = message;
        this.countDownLatch.countDown();
    }

    public long getBeginTimestamp() {
        return beginTimestamp;
    }

    public boolean isSendRequestOK() {
        return sendRequestOK;
    }

    public void setSendRequestOK(boolean sendRequestOK) {
        this.sendRequestOK = sendRequestOK;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public long getOpaque() {
        return opaque;
    }

}
