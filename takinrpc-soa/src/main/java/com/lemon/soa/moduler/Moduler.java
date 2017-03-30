package com.lemon.soa.moduler;

import com.lemon.soa.common.Constants;
import com.lemon.soa.common.URL;
import com.lemon.soa.common.spi.ExtensionLoader;
import com.lemon.soa.common.utils.NetUtils;
import com.lemon.soa.moduler.extension.blackwhite.IBlackWhite;
import com.lemon.soa.moduler.extension.degrade.IDegrade;
import com.lemon.soa.moduler.extension.echosound.IEchoSound;
import com.lemon.soa.moduler.extension.flowrate.IFlowRate;
import com.lemon.soa.moduler.extension.gracestop.IGraceStop;
import com.lemon.soa.moduler.extension.idempotent.Idempotent;
import com.lemon.soa.moduler.extension.pipescaling.IPipeScaling;
import com.lemon.soa.moduler.neure.INeure;

/**
 * 1.泛化引用、泛化实现<br>
 * 2.链路追踪、容量规划、实时监控<br>
 * 3.优雅停机→黑白名单→管道缩放→流量控制→资源鉴权→服务降级→幂等保障→灰度路由→回声探测→[熔断拒绝→超时控制→舱壁隔离→服务容错→慢性尝试]<br>
 * 
 * me
 * @version v1.0
 */
public class Moduler<REQ, RES> {

    /**
     * 统一配置资源
     */
    URL url = new URL(Constants.DEFAULT_PROTOCOL_VALUE, NetUtils.getLocalHostName(), Constants.DEFAULT_PORT_VALUE, this.getClass().getName());

    /**
     * 优雅停机
     */
    IGraceStop<REQ, RES> graceStop = ExtensionLoader.getExtensionLoader(IGraceStop.class).getAdaptiveExtension();
    /**
     * 黑白名单
     */
    IBlackWhite<REQ, RES> blackWhite = ExtensionLoader.getExtensionLoader(IBlackWhite.class).getAdaptiveExtension();
    /**
     * 管道缩放
     */
    IPipeScaling<REQ, RES> pipeScaling = ExtensionLoader.getExtensionLoader(IPipeScaling.class).getAdaptiveExtension();
    /**
     * 流量控制
     */
    IFlowRate<REQ, RES> flowRate = ExtensionLoader.getExtensionLoader(IFlowRate.class).getAdaptiveExtension();
    /**
     * 服务降级
     */
    IDegrade<REQ, RES> degrade = ExtensionLoader.getExtensionLoader(IDegrade.class).getAdaptiveExtension();
    /**
     * 幂等模块
     */
    Idempotent<REQ, RES> idempotent = ExtensionLoader.getExtensionLoader(Idempotent.class).getAdaptiveExtension();

    /**
     * 回声探测
     */
    IEchoSound<REQ, RES> echoSound = ExtensionLoader.getExtensionLoader(IEchoSound.class).getAdaptiveExtension();

    /**
     * 容错内核
     */
    INeure<REQ, RES> neure = ExtensionLoader.getExtensionLoader(INeure.class).getAdaptiveExtension();

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public IGraceStop<REQ, RES> getGraceStop() {
        return graceStop;
    }

    public void setGraceStop(IGraceStop<REQ, RES> graceStop) {
        this.graceStop = graceStop;
    }

    public IBlackWhite<REQ, RES> getBlackWhite() {
        return blackWhite;
    }

    public void setBlackWhite(IBlackWhite<REQ, RES> blackWhite) {
        this.blackWhite = blackWhite;
    }

    public IPipeScaling<REQ, RES> getPipeScaling() {
        return pipeScaling;
    }

    public void setPipeScaling(IPipeScaling<REQ, RES> pipeScaling) {
        this.pipeScaling = pipeScaling;
    }

    public IFlowRate<REQ, RES> getFlowRate() {
        return flowRate;
    }

    public void setFlowRate(IFlowRate<REQ, RES> flowRate) {
        this.flowRate = flowRate;
    }

    public IDegrade<REQ, RES> getDegrade() {
        return degrade;
    }

    public void setDegrade(IDegrade<REQ, RES> degrade) {
        this.degrade = degrade;
    }

    public Idempotent<REQ, RES> getIdempotent() {
        return idempotent;
    }

    public void setIdempotent(Idempotent<REQ, RES> idempotent) {
        this.idempotent = idempotent;
    }

    public IEchoSound<REQ, RES> getEchoSound() {
        return echoSound;
    }

    public void setEchoSound(IEchoSound<REQ, RES> echoSound) {
        this.echoSound = echoSound;
    }

    public INeure<REQ, RES> getNeure() {
        return neure;
    }

    public void setNeure(INeure<REQ, RES> neure) {
        this.neure = neure;
    }

}
