package com.lemon.soa.moduler.extension.echosound;

import com.lemon.soa.common.exception.echosound.EchoSoundException;
import com.lemon.soa.moduler.IModuler;

/**
 * 回声探测
 * <br>
 * 1.回声测试用于检测服务是否可用，回声测试按照正常请求流程执行，能够测试整个调用是否通畅，可用于监控。
 * 2.所有服务自动实现EchoService接口，只需将任意服务引用强制转型为EchoService，即可使用。
 * me
 */
public interface IEchoSound<REQ, RES> extends IModuler<REQ, RES> {

    RES echosound(EchoSoundType echoSoundType, REQ req, IEchoSoundProcessor<REQ, RES> processor, Object... args) throws EchoSoundException;

}
