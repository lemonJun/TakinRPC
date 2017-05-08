package test.tcc;

import com.takin.rpc.server.anno.ServiceImpl;
import com.takin.rpc.server.tcc.Compensable;

@ServiceImpl
@Compensable(interfaceClass = AcountService.class, cancellableKey = "acountcancle", confirmableKey = "acountconfirm")
public class AcountServiceImpl implements AcountService {
    
    @Override
    public boolean deal(long id) {
        return false;
    }
    
}
