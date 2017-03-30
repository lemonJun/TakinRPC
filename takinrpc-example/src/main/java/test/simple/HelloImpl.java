package test.simple;

import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl
public class HelloImpl implements Hello {

    @Override
    public String say(String name) {
        return "hello " + name;
    }

}
