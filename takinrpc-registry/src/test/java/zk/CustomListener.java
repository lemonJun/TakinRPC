package zk;

import java.util.List;

import com.github.zkclient.IZkChildListener;

public class CustomListener implements IZkChildListener {

    @Override
    public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
        
    }

}
