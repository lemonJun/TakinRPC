package test.simple;

import com.takin.rpc.server.ServiceDefine;

@ServiceDefine
public interface Hello {

    String say(String name);
}
