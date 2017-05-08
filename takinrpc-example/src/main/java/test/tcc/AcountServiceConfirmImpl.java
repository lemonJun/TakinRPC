package test.tcc;

import com.takin.rpc.server.anno.ServiceImpl;

@ServiceImpl(lookUP = "acountconfirm")
public class AcountServiceConfirmImpl implements AcountService {

    @Override
    public boolean deal(long id) {
        System.out.println("confirm" + id);

        return false;
    }

}
