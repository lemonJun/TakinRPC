package com.lemon.soa.moduler.neure;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lemon.soa.common.exception.neure.NeureAlarmException;
import com.lemon.soa.common.exception.neure.NeureBreathException;
import com.lemon.soa.common.exception.neure.NeureException;
import com.lemon.soa.common.exception.neure.NeureFaultTolerantException;
import com.lemon.soa.moduler.senior.alarm.AlarmType;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;

/**
 * 神经元
 * <br>
 * @see 注意事项:每一次实例化只能使用一次
 * me
 * @param <REQ> 请求对象
 * @param <RES> 响应对象
 */
public class Neuron<REQ, RES> extends HystrixCommand<RES> {

    private static final Logger logger = LoggerFactory.getLogger(Neuron.class);

    private final REQ req;//请求对象
    private final Object[] args;//其他参数
    private final int maxExecuteTimes;//最大执行次数(重试次数+默认执行次数)
    private final NeureEntity neureEntity;//配置信息
    private final INeureProcessor<REQ, RES> processor;//路由器
    private final CountDownLatch retryExecuteTimes;// 需要重试次数

    //通过线程传递参数 有更好的方式
    //线程参数:用于记录当前线程中的参数，以便于传递至其他线程中
    //    private final Map<String, String> threadContextMap = ThreadContext.getContext();

    public Neuron(REQ req, NeureEntity neureEntity, INeureProcessor<REQ, RES> processor, Object... args) {
        super(neureEntity.getSetter());
        this.req = req;
        this.neureEntity = neureEntity;
        this.processor = processor;
        this.args = args;
        this.maxExecuteTimes = neureEntity.getMaxRetryTimes() + 1;//最大允许执行次数
        this.retryExecuteTimes = new CountDownLatch(maxExecuteTimes);
    }

    /**
     * 执行器
     */
    protected RES run() throws Exception {
        RES res = null;
        try {
            if (neureEntity.isThreadContext()) {
                putThreadContextParameters();//线程之间参数传递
            }

            while (retryExecuteTimes.getCount() > 0) {
                long retryStartTime = System.currentTimeMillis();
                try {
                    return processor.processor(req, args);//执行processor
                } catch (Throwable t) {
                    //$NON-NLS-run-alarm$
                    doAlarm("run-alarm", AlarmType.RUN_ROUTE, t);

                    String routeErr = String.format("The run-route is failure, error is:%s", t.getMessage());
                    logger.error(routeErr, t);
                    t.printStackTrace();
                    if (retryExecuteTimes.getCount() < 2) {//最后一次重试,则向外抛异常
                        throw new Throwable(routeErr, t);
                    }
                } finally {
                    if (neureEntity.getMaxRetryTimes() > 0) {//需要重试
                        long nowExpend = System.currentTimeMillis() - retryStartTime;//计算本次重试耗时
                        try {
                            long breathTime = processor.breath(retryExecuteTimes.getCount(), nowExpend, maxExecuteTimes, args);//计算慢性休眠时间
                            if (retryExecuteTimes.getCount() > 1) {//非最后一次重试,都需要休眠;相反,最后一次重试则快速失败,不需要休眠
                                Thread.sleep(breathTime);//休眠后重试						
                            }
                        } catch (Throwable t) {
                            //$NON-NLS-run-breath$
                            doAlarm("run-breath", AlarmType.RUN_BREATH, t);

                            String breathErr = String.format("The run-breath is failure, error is:%s", t.getMessage());
                            logger.error(breathErr, t);
                            t.printStackTrace();
                            throw new NeureBreathException(breathErr, t);
                        }
                    }

                    retryExecuteTimes.countDown();//递减重试次数
                }
            }
        } catch (Throwable t) {//捕获重试完成后抛出的异常
            if (neureEntity.isFallback()) {//需要降级
                throw new NeureException(t.getMessage(), t);
            } else {//不需要降级
                throw new HystrixBadRequestException(t.getMessage(), t);
            }
        } finally {
            if (neureEntity.isThreadContext()) {
            }
        }

        return res;
    }

    /**
     * 失败容错
     */
    @Override
    protected RES getFallback() {
        try {
            if (neureEntity.isThreadContext()) {//线程之间参数传递
                putThreadContextParameters();
            }

            //容错处理
            return processor.faulttolerant(req, args);
        } catch (Throwable t) {
            //$NON-NLS-getFallback-faulttolerant$
            doAlarm("getFallback-faulttolerant", AlarmType.FALLBACK_FAULT_TOLERANT, t);

            String faulttolerantErr = String.format("The getFallback-faulttolerant is failure, error is:%s", t.getMessage());
            logger.error(faulttolerantErr, t);
            t.printStackTrace();
            throw new NeureFaultTolerantException(faulttolerantErr, t);
        } finally {
            if (neureEntity.isThreadContext()) {//清理新线程中的参数
            }
        }
    }

    /**
     * 执行告警
     * 
     * @param name
     * @param alarmType
     * @param t
     */
    public void doAlarm(String name, AlarmType alarmType, Throwable t) {
        try {
            processor.alarm(alarmType, req, null, t, args);
        } catch (Throwable th) {
            String alarmErr = String.format("The %s is failure, error is:%s", name, th.getMessage());
            logger.error(alarmErr, th);
            th.printStackTrace();
            throw new NeureAlarmException(alarmErr, th);
        }
    }

    /**
     * 重试执行次数
     * 
     * @return
     */
    public long getRetryExecuteTimes() {
        return maxExecuteTimes - retryExecuteTimes.getCount();
    }

    /**
     * 参数注入线程
     */
    private void putThreadContextParameters() {
        //        if (threadContextMap != null) {
        //            if (!threadContextMap.isEmpty()) {
        //                for (Map.Entry<String, String> entry : threadContextMap.entrySet()) {
        //                    ThreadContext.put(entry.getKey(), entry.getValue());
        //                }
        //            }
        //        }
    }

}
