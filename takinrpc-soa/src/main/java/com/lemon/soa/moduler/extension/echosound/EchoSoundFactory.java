package com.lemon.soa.moduler.extension.echosound;

import com.lemon.soa.Conf;
import com.lemon.soa.common.exception.echosound.EchoSoundException;
import com.lemon.soa.common.spi.Adaptive;
import com.lemon.soa.moduler.Moduler;

/**
 * 回声探测
 * 
 * me
 *
 * @param <REQ>
 * @param <RES>
 */
@Adaptive
public class EchoSoundFactory<REQ, RES> implements IEchoSound<REQ, RES> {

    Moduler<REQ, RES> moduler;
    /**
     * 开关
     */
    boolean echoSoundSwitch = true;

    @Override
    public void notify(Moduler<REQ, RES> moduler) {
        this.moduler = moduler;

        echoSoundSwitch = this.moduler.getUrl().getModulerParameter(Conf.ECHOSOUND, EchoSoundConf.SWITCH_KEY, EchoSoundConf.SWITCH_DEF_VAL);
    }

    @Override
    public void init() throws Throwable {

    }

    @Override
    public RES echosound(EchoSoundType echoSoundType, REQ req, IEchoSoundProcessor<REQ, RES> processor, Object... args) throws EchoSoundException {
        if (!echoSoundSwitch) {//开关校验
            return processor.processor(req, args);
        }

        switch (echoSoundType) {
            case REQ:
                REQ echoREQ = processor.$echo(req, args);//模拟请求
                return processor.processor(echoREQ, args);
            case RES:
                return processor.$rebound(req, args);//模拟响应
            default:
                return processor.processor(req, args);//费回声探测
        }
    }

    @Override
    public void destory() throws Throwable {

    }

}
