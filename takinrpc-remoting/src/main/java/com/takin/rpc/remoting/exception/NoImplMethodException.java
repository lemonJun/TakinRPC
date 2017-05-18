package com.takin.rpc.remoting.exception;

/**
 * Client连接Server失败，抛出此异常
 */
public class NoImplMethodException extends RemotingException {
    private static final long serialVersionUID = -5565366231695911316L;

    public NoImplMethodException(String classname, String methodname) {
        this(classname, methodname, null);
    }

    public NoImplMethodException(String classname, String methodname, Throwable cause) {
        super("invoke class: " + classname + " meehod: " + methodname + " failed", cause);
    }
}
