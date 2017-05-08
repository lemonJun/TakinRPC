package org.robotninjas.raft.rpc;

import com.google.common.util.concurrent.AbstractFuture;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

class ResponseHandler<T> extends AbstractFuture<T> implements RpcCallback<T> {

    private final RpcController controller;

    ResponseHandler(RpcController controller) {
        this.controller = controller;
    }

    @Override
    public void run(T parameter) {
        if (parameter == null) {
            setException(new Exception(controller.errorText()));
        } else {
            set(parameter);
        }
    }
}
