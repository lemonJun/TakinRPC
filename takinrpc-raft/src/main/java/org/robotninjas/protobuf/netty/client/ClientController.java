package org.robotninjas.protobuf.netty.client;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ClientController implements RpcController {

    private final NettyRpcChannel channel;
    private volatile String errorText = new String();
    private volatile boolean failed = false;
    private volatile boolean startCancelRequested;
    private volatile long timeout;

    public ClientController(NettyRpcChannel channel) {
        this.channel = checkNotNull(channel);
    }

    public void setTimeout(long timeout, TimeUnit unit) {
        this.timeout = MILLISECONDS.convert(timeout, unit);
    }

    public long getTimeoutMillis() {
        return timeout;
    }

    @Override
    public void reset() {
        this.errorText = new String();
        this.failed = false;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public String errorText() {
        return errorText;
    }

    @Override
    public void startCancel() {
        if (!startCancelRequested) {
            startCancelRequested = true;
            channel.requestCancel();
        }
    }

    @Override
    public void setFailed(String reason) {
        this.failed = true;
        this.errorText = reason;
    }

    @Override
    public boolean isCanceled() {
        throw new UnsupportedOperationException("Can only be called from server");
    }

    @Override
    public void notifyOnCancel(RpcCallback<Object> callback) {
        throw new UnsupportedOperationException("Can only be called from server");
    }

}
