package test;

import java.util.ArrayList;
import java.util.List;

import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl(isdefault = true)
public class HelloImpl2 implements Hello {

    @Override
    public String say(String name) {
        return "hello 333" + name;
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
