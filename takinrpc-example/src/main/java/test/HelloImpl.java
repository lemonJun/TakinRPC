package test;

import java.util.ArrayList;
import java.util.List;

import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl
public class HelloImpl implements Hello {

    @Override
    public String say(String name) throws Exception {
        return "hello  111" + name;
    }

    @Override
    public String auth(User u) {
        return "yes";
    }

    @Override
    public List<String> getall(User u) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        return list;
    }

}
