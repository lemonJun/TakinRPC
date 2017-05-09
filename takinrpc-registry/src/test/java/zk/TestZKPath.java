package zk;

import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.takin.rpc.zkclient.ZkClient;
import com.takin.rpc.zkclient.ZkUtils;

public class TestZKPath {

    private ZkClient zkClient;

    public void startup() {
        zkClient = new ZkClient("localhost:2181", 500, 500);
    }

    @Test
    public void create() {

    }

    @Test
    public void children() {
        startup();
        List<String> list = ZkUtils.getChildrenParentMayNotExist(zkClient, "/");

        System.out.println(JSON.toJSONString(list));
    }

}
