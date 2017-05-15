package test.asyn;

import com.takin.rpc.remoting.InvokeCallback;

public class HelloCallBack implements InvokeCallback {

    @Override
    public void operationComplete(Object obj) {
        System.out.println((String) obj);
    }
}
