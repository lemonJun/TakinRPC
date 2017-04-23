//package com.takin.rpc.remoting.netty5;
//
//import java.io.Serializable;
//import java.util.concurrent.atomic.AtomicLong;
//
//import com.takin.emmet.util.AddressUtil;
//
///**
// * netty 服务交互的实体类
// * 目前使用的是marshalling解码器   城朵实现序列化接口
// * 遇到一个很深的坑：marshalling不解序列化 Object[]对象   调试了好久才发现的
// * @author lemon
// * @version 1.0
// * @date  2015年9月7日 下午4:39:10
// * @see 
// * @since
// */
//public final class RemotingProtocol<T> implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//    
//    private static final AtomicLong RequestId = new AtomicLong(1);
//    private final String identity = AddressUtil.getLocalAddress();//这个是节点组的唯一标识符 可能是task也可能是job
//    private long start = System.currentTimeMillis();
//    private int flag = 0;
//    private final long opaque = RequestId.getAndIncrement();
//
//    /**-----------------*/
//    private Class<?> defineClass;//接口类
//    private Class<?> implClass;//实现类
//    private String method;//调用方法
//    private Object[] args;//方法参数
//    private Class<?>[] mParamsTypes;//方法的参数类型
//    private Class<?> mReturnType;
//    private T resultJson;
//
//    public String getUUID() {//此次调用的唯一标识
//        return String.format("%s-%d", getIdentity(), opaque);
//    }
//
//    public Class<?> getDefineClass() {
//        return defineClass;
//    }
//
//    public long getOpaque() {
//        return opaque;
//    }
//
//    public void setDefineClass(Class<?> defineClass) {
//        this.defineClass = defineClass;
//    }
//
//    public Class<?> getImplClass() {
//        return implClass;
//    }
//
//    public void setImplClass(Class<?> implClass) {
//        this.implClass = implClass;
//    }
//
//    public static AtomicLong getRequestid() {
//        return RequestId;
//    }
//
//    public String getMethod() {
//        return method;
//    }
//
//    public void setMethod(String method) {
//        this.method = method;
//    }
//
//    public Object[] getArgs() {
//        return args;
//    }
//
//    public void setArgs(Object[] args) {
//        this.args = args;
//    }
//
//    public Class<?>[] getmParamsTypes() {
//        return mParamsTypes;
//    }
//
//    public void setmParamsTypes(Class<?>[] mParamsTypes) {
//        this.mParamsTypes = mParamsTypes;
//    }
//
//    public long getStart() {
//        return start;
//    }
//
//    public void setStart(long start) {
//        this.start = start;
//    }
//
//    public int getFlag() {
//        return flag;
//    }
//
//    public void setFlag(int flag) {
//        this.flag = flag;
//    }
//
//    public T getResultJson() {
//        return resultJson;
//    }
//
//    public void setResultJson(T resultJson) {
//        this.resultJson = resultJson;
//    }
//
//    public String getIdentity() {
//        return identity;
//    }
//
//    public Class<?> getmReturnType() {
//        return mReturnType;
//    }
//
//    public void setmReturnType(Class<?> mReturnType) {
//        this.mReturnType = mReturnType;
//    }
//
//}
