package test.asyn;

import com.takin.rpc.remoting.InvokeCallback;
import com.takin.rpc.server.anno.ServiceDefine;

@ServiceDefine
public interface HelloAsyn {

    String say(String name, InvokeCallback callback) throws Exception;

} 
