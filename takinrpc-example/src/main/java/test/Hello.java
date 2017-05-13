package test;

import java.util.List;

import com.takin.rpc.server.anno.ServiceDefine;

@ServiceDefine
public interface Hello {

    String say(String name) throws Exception;

    String auth(User u);

    List<String> getall(User u);

}
