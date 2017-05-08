package test.tcc;

import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl(lookUP = "acountcancel")
public class AcountServiceCancelImpl implements AcountService {

    @Override
    public boolean deal(long id) {
        System.out.println("cancel" + id);

        return false;
    }

}
