package test.asyn;

import com.takin.rpc.remoting.InvokeCallback;
import com.takin.rpc.remoting.netty4.ResponseFuture;
import com.takin.rpc.remoting.nio.handler.IoFuture;

public class HelloCallBack implements InvokeCallback {

    @Override
    public void operationComplete(ResponseFuture responseFuture) {
        
    }

    @Override
    public void operationComplete(IoFuture responseFuture) {
        
    }

}
