package com.lemon.soa.moduler.extension.idempotent;

import com.lemon.soa.common.exception.idempotent.IdempotentException;
import com.lemon.soa.moduler.IModuler;

/**
 * 幂等机制(在一段时间内重复提交请求从而提高服务请求的质量,且多次提交的结果相同,对后端服务不会产生影响)<p>
 * <p>
 * 注意:<p>
 * 1.重复提交提升请求质量<p>
 * 2.重复提交结果相同<p>
 * <p>
 * 使用场景:
 * <p>
 * 在业务开发中，我们常会面对防止重复请求的问题。当服务端对于请求的响应涉及数据的修改，或状态的变更时，可能会造成极大的危害。重复请求的后果在交易系统、
 * 售后维权，以及支付系统中尤其严重。前台操作的抖动，快速操作，网络通信或者后端响应慢，都会增加后端重复处理的概率。
 * 
 * me
 */
public interface Idempotent<REQ, RES> extends IModuler<REQ, RES> {

    RES idempotent(String idempotentKEY, REQ req, IdempotentProcessor<REQ, RES> processor, Object... args) throws IdempotentException;

}
