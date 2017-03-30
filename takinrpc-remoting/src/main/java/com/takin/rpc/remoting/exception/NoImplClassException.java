package com.takin.rpc.remoting.exception;

/**
 * Client连接Server失败，抛出此异常
 */
public class NoImplClassException extends RemotingException {
    private static final long serialVersionUID = -5565366231695911316L;

    public NoImplClassException(String classname) {
        this(classname, null);
    }

    public NoImplClassException(String classname, Throwable cause) {
        super("invoke class: " + classname + " failed", cause);
    }
}
