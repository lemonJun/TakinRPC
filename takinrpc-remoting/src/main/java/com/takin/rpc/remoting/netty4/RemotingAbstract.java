package com.takin.rpc.remoting.netty4;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.emmet.concurrent.SemaphoreOnce;
import com.takin.rpc.remoting.InvokeCallback;
import com.takin.rpc.remoting.exception.RemotingConnectException;
import com.takin.rpc.remoting.exception.RemotingSendRequestException;
import com.takin.rpc.remoting.exception.RemotingTimeoutException;
import com.takin.rpc.remoting.exception.RemotingTooMuchRequestException;
import com.takin.rpc.remoting.util.RemotingHelper;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * 远程方法调用的抽象类 
 *
 * @author WangYazhou
 * @date  2017年2月6日 下午4:34:53
 * @see
 */
public abstract class RemotingAbstract {

    private static final Logger logger = LoggerFactory.getLogger(RemotingAbstract.class);
    protected final Semaphore semaphoreOneway;
    protected final Semaphore semaphoreAsync;

    public final static ConcurrentHashMap<Long, ResponseFuture> responseTable = new ConcurrentHashMap<Long, ResponseFuture>(256);

    public RemotingAbstract(final int permitsOneway, final int permitsAsync) {
        this.semaphoreOneway = new Semaphore(permitsOneway, true);
        this.semaphoreAsync = new Semaphore(permitsAsync, true);
    }

    //    abstract public ExecutorService getCallbackExecutor();

    /**
     * 发起同步调用
     * 其实后面也是异步的
     * 这个同步是指：可以携带结果返回
     * @param address
     * @param message
     * @param timeout
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    protected RemotingProtocol invokeSyncImpl(final Channel channel, final RemotingProtocol message, int timeout) throws Exception, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        //        final Stopwatch watch = Stopwatch.createStarted();
        try {
            final ResponseFuture responseFuture = new ResponseFuture(message.getOpaque(), timeout);
            //            logger.info("currentthread:" + Thread.currentThread().getName());
            //            logger.debug(String.format("create respnse future use:%s", watch.toString()));
            responseTable.put(message.getOpaque(), responseFuture);
            //            logger.debug(String.format("put future use:%s", watch.toString()));
            channel.writeAndFlush(message).addListener(new ChannelFutureListener() {
                //什么时候会触发这一个接口呢
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {
                    //此步代表发送操作成功   设置的sendrequest值是为了区分发送失败还是服务端处理失败的

                    if (f.isSuccess()) {
                        responseFuture.setSendRequestOK(true);
                        //                        logger.debug(String.format("operationcomplete use:%s", watch.toString()));
                        return;
                    } else {
                        responseFuture.setSendRequestOK(false);
                    }
                    //无结果 返回原因
                    responseTable.remove(message.getOpaque());
                    responseFuture.setCause(f.cause());
                    responseFuture.putResponse(null);
                    //                    logger.debug(String.format("operation no use:%s", watch.toString()));
                }
            });
            //            logger.info("currentthread:" + Thread.currentThread().getName());

            //            logger.debug(String.format("finish listener use:%s", watch.toString()));
            RemotingProtocol result = responseFuture.waitResponse();
            //            logger.info(String.format("wait response use:%s", watch.toString()));
            if (null == result) {
                if (responseFuture.isSendRequestOK()) {
                    throw new Exception("request timeout ");
                } else {
                    throw new Exception("request error");
                }
            }
            //            logger.info(String.format("invoke sync,use:%s", watch.toString()));
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 异步调用
     * @param channel
     * @param request
     * @param timeoutMillis
     * @param invokeCallback
     * @throws InterruptedException
     * @throws RemotingTooMuchRequestException
     * @throws RemotingTimeoutException
     * @throws RemotingSendRequestException
     */
    @SuppressWarnings("rawtypes")
    public void invokeAsyncImpl(final Channel channel, final RemotingProtocol request, final long timeoutMillis, final InvokeCallback invokeCallback) throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException {
        final long opaque = request.getOpaque();
        //        boolean acquired = this.semaphoreAsync.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
        final SemaphoreOnce once = new SemaphoreOnce(this.semaphoreAsync);
        final ResponseFuture responseFuture = new ResponseFuture(opaque, timeoutMillis, invokeCallback, once);
        responseTable.put(opaque, responseFuture);
        try {
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {
                    //此步代表发送操作成功   设置的sendrequest值是为了区分发送失败还是服务端处理失败的
                    if (f.isSuccess()) {
                        responseFuture.setSendRequestOK(true);
                        return;
                    } else {
                        responseFuture.setSendRequestOK(false);
                    }
                    responseFuture.putResponse(null);
                    responseTable.remove(opaque);
                    //                    try {
                    //                        executeInvokeCallback(responseFuture);
                    //                    } catch (Throwable e) {
                    //                        logger.warn("excute callback in writeAndFlush addListener, and callback throw", e);
                    //                    } finally {
                    //                        responseFuture.release();
                    //                    }
                    logger.warn("send a request command to channel <{}> failed.", RemotingHelper.parseChannelRemoteAddr(channel));
                }
            });
        } catch (Exception e) {
            responseFuture.release();
            logger.warn("send a request command to channel <" + RemotingHelper.parseChannelRemoteAddr(channel) + "> Exception", e);
            throw new RemotingSendRequestException(RemotingHelper.parseChannelRemoteAddr(channel), e);
        }

    }

    /**
     * execute callback in callback executor. If callback executor is null, run directly in current thread
     * @param responseFuture
     */
    //    private void executeInvokeCallback(final ResponseFuture responseFuture) {
    //        boolean runInThisThread = false;
    //        ExecutorService executor = this.getCallbackExecutor();
    //        if (executor != null) {
    //            try {
    //                executor.submit(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        try {
    //                            responseFuture.executeInvokeCallback();
    //                        } catch (Throwable e) {
    //                            logger.warn("execute callback in executor exception, and callback throw", e);
    //                        }
    //                    }
    //                });
    //            } catch (Exception e) {
    //                runInThisThread = true;
    //                logger.warn("execute callback in executor exception, maybe executor busy", e);
    //            }
    //        } else {
    //            runInThisThread = true;
    //        }
    //        if (runInThisThread) {
    //            try {
    //                responseFuture.executeInvokeCallback();
    //            } catch (Throwable e) {
    //                logger.warn("executeInvokeCallback Exception", e);
    //            }
    //        }
    //    }

    /**
     * 
     * @param channel
     * @param request
     * @param timeoutMillis
     * @throws InterruptedException
     * @throws RemotingTooMuchRequestException
     * @throws RemotingTimeoutException
     * @throws RemotingSendRequestException
     */
    public void invokeOnewayImpl(final Channel channel, final RemotingProtocol request, final long timeoutMillis) throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException {
        //        request.markOnewayRPC();//把一个标记   目前还未用到
        boolean acquired = this.semaphoreOneway.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
        if (acquired) {
            final SemaphoreOnce once = new SemaphoreOnce(this.semaphoreOneway);
            try {
                channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture f) throws Exception {
                        once.release();
                        if (!f.isSuccess()) {
                            logger.warn("send a request command to channel <" + channel.remoteAddress() + "> failed.");
                        }
                    }
                });
            } catch (Exception e) {
                once.release();
                logger.warn("write send a request command to channel <" + channel.remoteAddress() + "> failed.");
                throw new RemotingSendRequestException(RemotingHelper.parseChannelRemoteAddr(channel), e);
            }
        } else {
            if (timeoutMillis <= 0) {
                throw new RemotingTooMuchRequestException("invokeOnewayImpl invoke too fast");
            } else {
                String info = String.format("invokeOnewayImpl tryAcquire semaphore timeout, %dms, waiting thread nums: %d semaphoreAsyncValue: %d", timeoutMillis, this.semaphoreOneway.getQueueLength(), this.semaphoreOneway.availablePermits());
                logger.warn(info);
                throw new RemotingTimeoutException(info);
            }
        }
    }

}
