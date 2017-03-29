package com.takin.rpc.remoting.netty5;

/**
 * RPC消息类型
 *
 *
 * @author lemon
 * @version 1.0
 * @date  2015年9月19日 上午11:27:35
 * @see 
 * @since
 */
public enum MessageType {

    TASK_PULL(0), //拉取任务
    TASK_PULL_RES(1), //拉取任务的返回
    NodeGroupPush(10), //新增节点
    TASK_LOG(20), //任务日志
    TASK_LOG_RES(21), //任务日志返回
    TASK_FINISH(30), //完成任务
    TASK_FINISH_RES(31), //完成任务返回

    REMOTING_INVOKE(50), //远程调用
    HEARTBEAT(40), //心跳
    HEARTBEAT_RES(41);//心跳返回

    private int value;

    private MessageType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
