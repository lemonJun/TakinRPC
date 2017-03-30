package com.lemon.soa.moduler.extension.degrade;

import com.lemon.soa.common.exception.degrade.DegradeException;
import com.lemon.soa.moduler.IModuler;

/**
 * 服务降级
 * <br>
 * 服务降级分类:<br>
 * 1.直接屏蔽降级<br>
 * 2.快速容错降级<br>
 * 3.自定义业务降级
 * <br>
 * 降级策略:<br>
 * 1.返回空<br>
 * 2.抛异常<br>
 * 3.本地mock<br>
 * 4.自定义策略
 * 
 * me
 */
public interface IDegrade<REQ, RES> extends IModuler<REQ, RES> {

    /**
     * 服务降级
     * 
     * @param req
     * @param processor
     * @param args
     * @return
     * @throws DegradeException
     */
    RES degrade(REQ req, IDegradeProcessor<REQ, RES> processor, Object... args) throws DegradeException;

}
