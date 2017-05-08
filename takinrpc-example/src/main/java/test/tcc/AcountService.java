package test.tcc;

import com.takin.rpc.server.anno.ServiceDefine;

@ServiceDefine
public interface AcountService {

    public abstract boolean deal(long id);

}
