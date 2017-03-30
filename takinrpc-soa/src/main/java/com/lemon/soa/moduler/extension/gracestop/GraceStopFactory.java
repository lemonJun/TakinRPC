package com.lemon.soa.moduler.extension.gracestop;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.lemon.soa.Conf;
import com.lemon.soa.common.NamedThreadFactory;
import com.lemon.soa.common.concurrent.LongAdder;
import com.lemon.soa.common.exception.gracestop.GraceStopedException;
import com.lemon.soa.common.exception.gracestop.GraceWaitStopException;
import com.lemon.soa.common.spi.Adaptive;
import com.lemon.soa.moduler.Moduler;
import com.lemon.soa.moduler.extension.flowrate.GraceStopBootType;

/**
 * 优雅停机
 * 
 * me
 * @version v1.0
 */
@Adaptive
public class GraceStopFactory<REQ, RES> implements IGraceStop<REQ, RES> {

    /**
     * 模块中心
     */
    private Moduler<REQ, RES> moduler;
    private GraceStopStatusType graceStopStatusType;

    /**
     * 实时流量计数器
     */
    private final LongAdder longAdder = new LongAdder();
    private ScheduledExecutorService scheduledExecutorService = null;

    @Override
    public void notify(Moduler<REQ, RES> moduler) {
        this.moduler = moduler;

        String status = moduler.getUrl().getModulerParameter(Conf.GRACESTOP, GraceStopConf.STATUS_KEY, GraceStopConf.STATUS_DEFAULT_VALUE.getVal());

        try {
            graceStopStatusType = GraceStopStatusType.valueOf(status);
        } catch (Exception e) {
            throw new GraceStopedException("Unknown GraceStop StatusType.");
        }

        //每次变更通知后进行检查
        this.schedule(moduler.getUrl().getModulerParameter(Conf.GRACESTOP, GraceStopConf.SHUTDOWN_TIMEOUT_KEY, GraceStopConf.SHUTDOWN_TIMEOUT_DEFAULT_VALUE));
    }

    @Override
    public void init() throws Throwable {
        //		try {
        //    		moduler.setUrl(moduler.getUrl().addParameter(GraceStopConf.STARTUP_TIME_KEY, System.currentTimeMillis()));//启动时间
        //		} catch (Throwable t) {
        //			t.printStackTrace();
        //		}
    }

    @Override
    public RES gracestop(REQ req, IGraceStopProcessor<REQ, RES> processor, Object... args) {
        //开机状态校验
        if (GraceStopStatusType.OFFLINE == graceStopStatusType) {//已离线
            throw new GraceStopedException();
        }

        //开/关机开关校验
        String bootOrStop = moduler.getUrl().getModulerParameter(Conf.GRACESTOP, GraceStopConf.SWITCH_KEY, GraceStopConf.SWITCH_DEFAULT_VALUE.getVal());
        if (GraceStopBootType.BOOT.getVal().equals(bootOrStop)) {//正常开机
            try {
                longAdder.increment();//加流量
                return processor.processor(req, args);
            } finally {
                longAdder.decrement();//减流量
            }
        } else {//等待关机,拒绝接入流量
            throw new GraceWaitStopException("Wait for shutdown, deny access to traffic.");
        }
    }

    private boolean schedule(long delay) {
        String bootOrStop = moduler.getUrl().getModulerParameter(Conf.GRACESTOP, GraceStopConf.SWITCH_KEY, GraceStopConf.SWITCH_DEFAULT_VALUE.getVal());
        if (GraceStopBootType.BOOT.getVal().equals(bootOrStop)) {//正常开机
            return false;
        }

        ScheduledFuture<Boolean> scheduledFuture = null;
        try {
            if (scheduledExecutorService == null) {
                scheduledExecutorService = Executors.newScheduledThreadPool(1, new NamedThreadFactory("GracestopFactoryRetryTimer", true));
            }
            scheduledFuture = scheduledExecutorService.schedule(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    //开/关机开关校验
                    String bootOrStop = moduler.getUrl().getModulerParameter(Conf.GRACESTOP, GraceStopConf.SWITCH_KEY, GraceStopConf.SWITCH_DEFAULT_VALUE.getVal());
                    if (GraceStopBootType.BOOT.getVal().equals(bootOrStop)) {//正常开机

                        String status = moduler.getUrl().getModulerParameter(Conf.GRACESTOP, GraceStopConf.STATUS_KEY, GraceStopConf.STATUS_DEFAULT_VALUE.getVal());
                        if (GraceStopStatusType.ONLINE.getVal().equals(status)) {//已停机
                            moduler.setUrl(moduler.getUrl().addModulerParameter(Conf.GRACESTOP, GraceStopConf.SHUTDOWN_TIME_KEY, System.currentTimeMillis()));//停机时间

                            return true;
                        }
                    }
                    return false;
                }
            }, delay, TimeUnit.MILLISECONDS);

            return scheduledFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdown();
            }
        }
    }

    @Override
    public void destory() throws Throwable {

    }

}
