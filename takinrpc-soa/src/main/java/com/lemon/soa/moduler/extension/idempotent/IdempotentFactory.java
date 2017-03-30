package com.lemon.soa.moduler.extension.idempotent;

import com.lemon.soa.Conf;
import com.lemon.soa.common.exception.idempotent.IdempotentContainException;
import com.lemon.soa.common.exception.idempotent.IdempotentException;
import com.lemon.soa.common.spi.Adaptive;
import com.lemon.soa.moduler.Moduler;

/**
 * 幂等处理中心
 * 
 * me
 *
 * @param <REQ>
 * @param <RES>
 */
@Adaptive
public class IdempotentFactory<REQ, RES> implements Idempotent<REQ, RES> {

    Moduler<REQ, RES> moduler = null;
    /**
     * 幂等总开关
     */
    boolean idempotentSwitch = false;
    /**
     * 持久化开关
     */
    boolean storageSwitch = false;
    /**
     * 如果资源已存在是否抛异常进行处理,true则抛异常,false则不抛异常而获取结果
     */
    boolean exception = false;

    @Override
    public void notify(Moduler<REQ, RES> moduler) {
        this.moduler = moduler;

        idempotentSwitch = this.moduler.getUrl().getModulerParameter(Conf.IDEMPOTENT, IdempotentConf.IDEMPOTENT_SWITCH_KEY, IdempotentConf.IDEMPOTENT_SWITCH_DEF_VAL);
        storageSwitch = this.moduler.getUrl().getModulerParameter(Conf.IDEMPOTENT, IdempotentConf.STORAGE_SWITCH_KEY, IdempotentConf.STORAGE_SWITCH_DEF_VAL);
        exception = this.moduler.getUrl().getModulerParameter(Conf.IDEMPOTENT, IdempotentConf.EXCEPTION_RES_SWITCH_KEY, IdempotentConf.EXCEPTION_RES_SWITCH_DEF_VAL);

    }

    @Override
    public void init() throws Throwable {

    }

    @Override
    public RES idempotent(String idempotentKEY, REQ req, IdempotentProcessor<REQ, RES> processor, Object... args) throws IdempotentException {
        if (!idempotentSwitch) {//幂等开关
            return processor.processor(req, args);
        }

        if (processor.check(idempotentKEY)) {//存在
            if (exception) {//抛异常进行重复请求的处理
                throw new IdempotentContainException();
            } else {//获取缓存结果进行重复请求的处理
                return processor.get(idempotentKEY);
            }
        } else {//不存在
            RES res = null;
            try {
                return res = processor.processor(req, args);
            } finally {
                if (storageSwitch) {//需要持久化
                    processor.storage(req, res, args);
                }
            }
        }
    }

    @Override
    public void destory() throws Throwable {

    }

}
