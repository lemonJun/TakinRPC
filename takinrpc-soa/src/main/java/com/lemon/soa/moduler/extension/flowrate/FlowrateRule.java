package com.lemon.soa.moduler.extension.flowrate;

/**
 * 流量控制规则实体
 * me
 */
public class FlowrateRule {

    /**
     * 流速控制总开关
     */
    private boolean flowrateSwitch = false;

    //$NON-NLS-并发控制$
    /**
     * 并发控制子开关
     */
    private boolean cctSwitch = false;
    /**
     * 最大允许并发数
     */
    private int permits = FlowRateConf.CCT_NUM_DEF_VAL;

    //$NON-NLS-流速控制$
    /**
     * QPS控制子开关
     */
    private boolean qpsSwitch = false;
    /**
     * 最大QPS数量
     */
    private double permitsPerSecond = FlowRateConf.QPS_NUM_DEF_VAL;

    public FlowrateRule() {
    }

    public FlowrateRule(boolean flowrateSwitch, boolean cctSwitch, int permits, boolean qpsSwitch, double permitsPerSecond) {
        this.flowrateSwitch = flowrateSwitch;
        this.cctSwitch = cctSwitch;
        this.permits = permits;
        this.qpsSwitch = qpsSwitch;
        this.permitsPerSecond = permitsPerSecond;
    }

    public boolean isFlowrateSwitch() {
        return flowrateSwitch;
    }

    public void setFlowrateSwitch(boolean flowrateSwitch) {
        this.flowrateSwitch = flowrateSwitch;
    }

    public boolean isCctSwitch() {
        return cctSwitch;
    }

    public void setCctSwitch(boolean cctSwitch) {
        this.cctSwitch = cctSwitch;
    }

    public int getPermits() {
        return permits;
    }

    public void setPermits(int permits) {
        this.permits = permits;
    }

    public boolean isQpsSwitch() {
        return qpsSwitch;
    }

    public void setQpsSwitch(boolean qpsSwitch) {
        this.qpsSwitch = qpsSwitch;
    }

    public double getPermitsPerSecond() {
        return permitsPerSecond;
    }

    public void setPermitsPerSecond(double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
    }

    @Override
    public String toString() {
        return "FlowrateRule [flowrateSwitch=" + flowrateSwitch + ", cctSwitch=" + cctSwitch + ", permits=" + permits + ", qpsSwitch=" + qpsSwitch + ", permitsPerSecond=" + permitsPerSecond + "]";
    }

}
