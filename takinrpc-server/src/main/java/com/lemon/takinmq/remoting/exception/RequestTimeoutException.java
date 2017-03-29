package com.lemon.takinmq.remoting.exception;

/**
 * @author Robert HG (254963746@qq.com) on 5/30/15.
 */
public class RequestTimeoutException extends RuntimeException {

    private static final long serialVersionUID = -4862929911390773246L;

    public RequestTimeoutException() {
        super();
    }

    public RequestTimeoutException(String message) {
        super(message);
    }

    public RequestTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestTimeoutException(Throwable cause) {
        super(cause);
    }

    protected RequestTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
