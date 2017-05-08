package test.tcc;

import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl(name = "acountcancle")
public class AcountServiceCancelImpl implements AcountService {

    @Override
    public boolean deal(long id) {
        return false;
    }

}
