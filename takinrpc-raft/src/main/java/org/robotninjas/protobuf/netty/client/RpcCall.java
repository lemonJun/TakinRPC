package org.robotninjas.protobuf.netty.client;

import com.google.common.util.concurrent.AbstractFuture;
import org.robotninjas.protobuf.netty.NettyRpcProto;
import org.robotninjas.protobuf.netty.RpcException;

class RpcCall extends AbstractFuture<NettyRpcProto.RpcResponse> {

    private static final long DEFAULT_TIMEOUT = 100000;

    private final NettyRpcProto.RpcRequest request;
    private final long timeoutMillis;

    public RpcCall(NettyRpcProto.RpcRequest request, long timeoutMillis) {
        this.request = request;
        this.timeoutMillis = timeoutMillis;
    }

    public RpcCall(NettyRpcProto.RpcRequest request) {
        this(request, DEFAULT_TIMEOUT);
    }

    public NettyRpcProto.RpcRequest getRequest() {
        return request;
    }

    public void complete(NettyRpcProto.RpcResponse response) {
        if (response.hasErrorCode()) {
            setException(new RpcException(getRequest(), response.getErrorMessage()));
        }
        set(response);
    }

    public void fail(Throwable e) {
        setException(e);
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }
}
