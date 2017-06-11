package test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.alibaba.fastjson.JSON;
import com.takin.emmet.reflect.RMethodUtils;
import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl
public class HelloImpl implements Hello {

    @Override
    public String say(String name) throws Exception {
        return "hello  111" + name;
    }

    @Override
    public List<String> getall(User u) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        return list;
    }

    @Override
    public String hi(int i) {
        return i * i + "";
    }

    public static void main(String[] args) {
        PropertyConfigurator.configure("conf/log4j.properties");
        Method[] methods = HelloImpl.class.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
            Class<?>[] param = method.getParameterTypes();
            System.out.println(JSON.toJSONString(param));
            try {
                Method m2 = RMethodUtils.searchMethod(HelloImpl.class, method.getName(), param);
                if (m2 == null) {
                    System.out.println("null");
                } else {
                    System.out.println(method.getName());
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
