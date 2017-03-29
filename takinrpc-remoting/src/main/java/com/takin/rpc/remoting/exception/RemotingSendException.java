package com.takin.rpc.remoting.exception;

/**
 * @author Robert HG (254963746@qq.com) on 8/18/14.
 */
public class RemotingSendException extends Exception {

    private static final long serialVersionUID = 409735306182141050L;

    public RemotingSendException() {
        super();
    }

    public RemotingSendException(String message) {
        super(message);
    }

    public RemotingSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemotingSendException(Throwable cause) {
        super(cause);
    }

    protected RemotingSendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
