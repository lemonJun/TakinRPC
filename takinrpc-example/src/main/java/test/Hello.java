package test;

import com.takin.rpc.server.anno.ServiceDefine;

@ServiceDefine
public interface Hello {

    String say(String name);
}
