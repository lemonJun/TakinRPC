package zk;

import java.util.List;

import com.takin.rpc.zkclient.IZkChildListener;

public class CustomListener implements IZkChildListener {

    @Override
    public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
        
    }

}
