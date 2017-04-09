package com.raft;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import com.raft.domain.RequestInfo;
import com.raft.future.BasicFuture;

import io.netty.channel.Channel;

/**
 * Created by Administrator on 2016/10/31.
 */
public class RequestManager {
    private final Map<Long, BasicFuture> reqs;

    private final AtomicLong requestId;

    public RequestManager() {
        reqs = new ConcurrentHashMap<Long, BasicFuture>();

        requestId = new AtomicLong(0);
    }

    public void add(long requestId, BasicFuture basicFuture) {
        reqs.put(requestId, basicFuture);
    }

    public void remove(long requestId) {
        reqs.remove(requestId);
    }

    public BasicFuture get(long requestId) {
        return reqs.get(requestId);
    }

    //同步请求
    public Object syncRequest(Channel channel, Object obj) throws ExecutionException, InterruptedException {
        return asyncRequest(channel, obj).get();
    }

    //异步请求
    public BasicFuture asyncRequest(Channel channel, Object obj) {
        //递增requestId
        final long requestVal = requestId.incrementAndGet();

        BasicFuture future = new BasicFuture();
        add(requestVal, future);

        //请求数据包
        RequestInfo reqInfo = new RequestInfo();
        reqInfo.setService(obj);
        reqInfo.setAddr(channel.localAddress().toString());
        reqInfo.setRequestId(requestVal);

        //发送
        channel.writeAndFlush(reqInfo);

        return future;
    }
}
