package com.lemon.soa.moduler.neure;

/**
 * 常用Hystrix参数
 * 
 * me
 * @version v1.0
 */
public class HystrixSetter {

    private String commandGroupKey = "neural-command-group-key";//Command分组KEY
    private String commandKey = "neural-command-key";//Command KEY

    private int eitTimeout = 1000;//执行隔离线程超时毫秒,默认为1000ms
    private int etimeout = 1000;//执行超时时间,默认为1000ms

    //调用线程允许请求HystrixCommand.GetFallback()的最大数量，默认10。超出时将会有异常抛出，注意：该项配置对于THREAD隔离模式也起作用
    private int fismRequests = 10;

    ////$NON-NLS-断路器$
    private int cbRequests = 20;//当在配置时间窗口内达到此数量的失败后,进行短路,默认20个
    private int cbSleepWindow = 5;//短路多久以后开始尝试是否恢复,默认5s
    private int cbErrorRate = 50;//出错百分比阈值,当达到此阈值后,开始短路,默认50%

    ////$NON-NLS-线程池KEY$
    private String threadPoolKey = "neural-threadpool-key";//线程池KEY
    private int threadPoolCoreSize = 10;//线程池设置:线程池核心线程数,默认为10
    private int threadPoolQueueSize = 5;//排队线程数量阈值，默认为5，达到时拒绝，如果配置了该选项，队列的大小是该队列

    //$NON-NLS-默认参数$
    /**
     * 标准版配置
     * @return
     */
    public static HystrixSetter build() {
        return new HystrixSetter();
    }

    /**
     * 较高版配置
     * @return
     */
    public static HystrixSetter buildSenior() {
        return new HystrixSetter(80000, 80000, 30, 50, 50, 10);
    }

    /**
     * 高级版配置
     * @return
     */
    public static HystrixSetter buildSuper() {
        return new HystrixSetter(80000, 80000, 50, 50, 100, 15);
    }

    public HystrixSetter() {
    }

    public HystrixSetter(int eitTimeout, int etimeout, int cbRequests, int cbErrorRate, int threadPoolCoreSize, int threadPoolQueueSize) {
        this.eitTimeout = eitTimeout;
        this.etimeout = etimeout;
        this.cbRequests = cbRequests;
        this.cbErrorRate = cbErrorRate;
        this.threadPoolCoreSize = threadPoolCoreSize;
        this.threadPoolQueueSize = threadPoolQueueSize;
    }

    public String getCommandGroupKey() {
        return commandGroupKey;
    }

    public void setCommandGroupKey(String commandGroupKey) {
        this.commandGroupKey = commandGroupKey;
    }

    public String getCommandKey() {
        return commandKey;
    }

    public void setCommandKey(String commandKey) {
        this.commandKey = commandKey;
    }

    public int getEitTimeout() {
        return eitTimeout;
    }

    public void setEitTimeout(int eitTimeout) {
        this.eitTimeout = eitTimeout;
    }

    public int getEtimeout() {
        return etimeout;
    }

    public void setEtimeout(int etimeout) {
        this.etimeout = etimeout;
    }

    public int getFismRequests() {
        return fismRequests;
    }

    public void setFismRequests(int fismRequests) {
        this.fismRequests = fismRequests;
    }

    public int getCbRequests() {
        return cbRequests;
    }

    public void setCbRequests(int cbRequests) {
        this.cbRequests = cbRequests;
    }

    public int getCbSleepWindow() {
        return cbSleepWindow;
    }

    public void setCbSleepWindow(int cbSleepWindow) {
        this.cbSleepWindow = cbSleepWindow;
    }

    public int getCbErrorRate() {
        return cbErrorRate;
    }

    public void setCbErrorRate(int cbErrorRate) {
        this.cbErrorRate = cbErrorRate;
    }

    public String getThreadPoolKey() {
        return threadPoolKey;
    }

    public void setThreadPoolKey(String threadPoolKey) {
        this.threadPoolKey = threadPoolKey;
    }

    public int getThreadPoolCoreSize() {
        return threadPoolCoreSize;
    }

    public void setThreadPoolCoreSize(int threadPoolCoreSize) {
        this.threadPoolCoreSize = threadPoolCoreSize;
    }

    public int getThreadPoolQueueSize() {
        return threadPoolQueueSize;
    }

    public void setThreadPoolQueueSize(int threadPoolQueueSize) {
        this.threadPoolQueueSize = threadPoolQueueSize;
    }

}
