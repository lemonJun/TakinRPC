package test.simple;

import com.takin.rpc.server.ServiceImpl;

@ServiceImpl
public class HelloImpl implements Hello {

    @Override
    public String say(String name) {
        return "hello " + name;
    }

}
