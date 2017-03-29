package com.takin.rpc.remoting.netty5;

import java.util.concurrent.atomic.AtomicLong;

/**
 * netty 服务交互的实体类
 * 目前使用的是marshalling解码器   城朵实现序列化接口
 * 遇到一个很深的坑：marshalling不解序列化 Object[]对象   调试了好久才发现的
 * @author lemon
 * @version 1.0
 * @date  2015年9月7日 下午4:39:10
 * @see 
 * @since
 */
public final class RemotingProtocol<T> {

    private static AtomicLong RequestId = new AtomicLong(1);
    private String clazz;
    private String method;
    private Object[] args;
    private Class<?>[] mParamsTypes;

    private int type;// 消息类型 对应messagetype中的枚举类型
    private Long timestamp = System.currentTimeMillis();
    private long opaque = RequestId.getAndIncrement();
    private String identity;//这个是节点组的唯一标识符 可能是task也可能是job
    private int flag = 0;
    private T resultJson;
    private int version = 0;

    public String getUuid() {
        return String.format("%s-%d", getIdentity(), getOpaque());
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Class<?>[] getmParamsTypes() {
        return mParamsTypes;
    }

    public void setmParamsTypes(Class<?>[] mParamsTypes) {
        this.mParamsTypes = mParamsTypes;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public long getOpaque() {
        return opaque;
    }

    public void setOpaque(long opaque) {
        this.opaque = opaque;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public T getResultJson() {
        return resultJson;
    }

    public void setResultJson(T resultJson) {
        this.resultJson = resultJson;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    private String nodeGroup;
    private String nodeType;

    public String getNodeGroup() {
        return nodeGroup;
    }

    public void setNodeGroup(String nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

}
