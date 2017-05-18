package test;

import java.util.List;

import com.takin.rpc.server.anno.ServiceDefine;

@ServiceDefine
public interface Hello {

    String say(String name) throws Exception;

    String hi(int i);

    List<String> getall(User u);

}
