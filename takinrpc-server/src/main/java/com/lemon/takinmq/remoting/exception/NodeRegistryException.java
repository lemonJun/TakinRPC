package com.lemon.takinmq.remoting.exception;

/**
 * @author Robert HG (254963746@qq.com) on 5/17/15.
 */
public class NodeRegistryException extends RuntimeException {

    private static final long serialVersionUID = -9213305086356940794L;

    public NodeRegistryException() {
        super();
    }

    public NodeRegistryException(String message) {
        super(message);
    }

    public NodeRegistryException(String message, Throwable cause) {
        super(message, cause);
    }

    public NodeRegistryException(Throwable cause) {
        super(cause);
    }

    protected NodeRegistryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
