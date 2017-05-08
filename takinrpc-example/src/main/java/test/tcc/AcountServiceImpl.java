package test.tcc;

import com.takin.rpc.server.anno.ServiceImpl;
import com.takin.rpc.server.tcc.Compensable;

@ServiceImpl(isdefault = true)
@Compensable(interfaceClass = AcountService.class, cancellableKey = "acountcancel", confirmableKey = "acountconfirm")
public class AcountServiceImpl implements AcountService {

    @Override
    public boolean deal(long id) {
        System.out.println("deal" + id);
        return false;
    }

}
