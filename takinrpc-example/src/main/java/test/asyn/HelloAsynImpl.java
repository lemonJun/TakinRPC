package test.asyn;

import com.takin.rpc.remoting.InvokeCallback;

public class HelloAsynImpl implements HelloAsyn {

    @Override
    public String say(String name, InvokeCallback callback) throws Exception {
        return name + " back";
    }
    
}
