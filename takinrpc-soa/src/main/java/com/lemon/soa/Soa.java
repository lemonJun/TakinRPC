package com.lemon.soa;

import java.util.List;
import java.util.Map;

import com.lemon.soa.chain.INeuralChain;
import com.lemon.soa.common.spi.ExtensionLoader;
import com.lemon.soa.moduler.Moduler;
import com.lemon.soa.moduler.extension.echosound.EchoSoundType;
import com.lemon.soa.processor.INeuralProcessor;

/**
 * 微服务神经元 <br>
 * <br>
 * 核心：限流、降级、熔断<br>
 * 扩展：优雅停机、超时控制、重试机制、故障转移、管道缩放、黑白名单<br>
 * <br>
 * 限流：份儿
 * <br>
 * <br>
 * 注意:使用时请单例化使用<br>
 * 1.泛化引用、泛化实现<br>
 * 2.链路追踪、容量规划、实时监控<br>
 * 3.优雅停机→黑白名单→管道缩放→流量控制→资源鉴权→服务降级→幂等保障→灰度路由→回声探测→[熔断拒绝→超时控制→舱壁隔离→服务容错→慢性尝试]<br>
 * <br>
 * 待实现: <br>
 * 链路追踪<br>
 * 容量规划<br>
 * 实时监控<br>
 * 资源鉴权<br>
 * 灰度路由<br>
 * 
 * me
 *
 * @param <REQ> 请求对象
 * @param <RES> 响应对象
 */
public class Soa<REQ, RES> extends AbstractNeuralFactory<REQ, RES> {

    private List<INeuralChain<REQ, RES>> neuralChains = ExtensionLoader.getExtensionLoader(INeuralChain.class).getAdaptiveExtensions();

    public Soa(Moduler<REQ, RES> moduler) {
        try {
            super.notify(moduler);// 通知节点配置信息
            super.init();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        for (int k = 0; k < neuralChains.size(); k++) {
            INeuralChain<REQ, RES> neuralChain = neuralChains.get(k);// 当前责任人
            INeuralChain<REQ, RES> nextNeuralChain = null;// 下一责任人
            if (k + 1 < neuralChains.size()) {
                nextNeuralChain = neuralChains.get(k + 1);
            }
            // $NON-NLS-责任链链接$
            // 优雅停机 --> 黑白名单 --> 管道缩放 --> 流量控制 --> 服务降级 --> 幂等机制 --> 回声探测 -->
            // 服务容错(熔断拒绝→超时控制→舱壁隔离→服务容错→慢性尝试)
            neuralChain.initChain(moduler, nextNeuralChain);
        }

    }

    /**
     * 微服务神经元
     * 
     * @param req 请求对象
     * @param neuralId 神经元请求ID(用于幂等控制)
     * @param echoSoundType 回声探测类型
     * @param blackWhiteIdKeyVals 黑白名单KEY-VALUE
     * @param processor 微服务神经元处理器
     * @param args 其他参数
     * @return
     */
    public RES neural(REQ req, String neuralId, EchoSoundType echoSoundType, Map<String, Object> blackWhiteIdKeyVals, INeuralProcessor<REQ, RES> processor, Object... args) {

        // $NON-NLS-通过优雅停机开始进入模块链$
        return neuralChains.get(0).chain(req, neuralId, echoSoundType, blackWhiteIdKeyVals, processor, args);
    }

}