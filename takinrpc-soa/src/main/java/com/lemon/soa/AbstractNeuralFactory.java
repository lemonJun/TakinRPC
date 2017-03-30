package com.lemon.soa;

import com.lemon.soa.common.URL;
import com.lemon.soa.moduler.IModuler;
import com.lemon.soa.moduler.Moduler;

/**
 * 微服务神经元抽象工厂
 * 
 * me
 * @version v1.0
 */
public abstract class AbstractNeuralFactory<REQ, RES> implements IModuler<REQ, RES>, INotify<URL> {

    protected Moduler<REQ, RES> moduler = null;

    /**
     * 初始化
     */
    @Override
    public void init() throws Throwable {
        this.moduler.getGraceStop().init();
        this.moduler.getBlackWhite().init();
        this.moduler.getPipeScaling().init();
        this.moduler.getFlowRate().init();
        this.moduler.getDegrade().init();
        this.moduler.getIdempotent().init();
        this.moduler.getEchoSound().init();
        this.moduler.getNeure().init();
    }

    /**
     * 通知变更广播
     */
    @Override
    public void notify(URL msg) {
        moduler.setUrl(msg);
        this.notify(moduler);
    }

    /**
     * 通知变更
     */
    @Override
    public void notify(Moduler<REQ, RES> moduler) {
        this.moduler = moduler;

        //$NON-NLS-按照顺序$
        this.moduler.getGraceStop().notify(this.moduler);
        this.moduler.getBlackWhite().notify(this.moduler);
        this.moduler.getPipeScaling().notify(this.moduler);
        this.moduler.getFlowRate().notify(this.moduler);
        this.moduler.getDegrade().notify(this.moduler);
        this.moduler.getIdempotent().notify(this.moduler);
        this.moduler.getEchoSound().notify(this.moduler);
        this.moduler.getNeure().notify(this.moduler);
    }

    /**
     * 销毁
     */
    @Override
    public void destory() throws Throwable {
        this.moduler.getGraceStop().destory();
        this.moduler.getBlackWhite().destory();
        this.moduler.getPipeScaling().destory();
        this.moduler.getFlowRate().destory();
        this.moduler.getDegrade().destory();
        this.moduler.getIdempotent().destory();
        this.moduler.getEchoSound().destory();
        this.moduler.getNeure().destory();
    }

}
