package com.lemon.soa.common.exception.idempotent;

/**
 * 中枢神经异常
 * 
 * me
 */
public class IdempotentException extends RuntimeException {

    private static final long serialVersionUID = 7815426752583648734L;

    public static final int UNKNOWN_EXCEPTION = 0;
    public static final int NETWORK_EXCEPTION = 1;
    public static final int TIMEOUT_EXCEPTION = 2;
    public static final int BIZ_EXCEPTION = 3;
    public static final int FORBIDDEN_EXCEPTION = 4;
    public static final int SERIALIZATION_EXCEPTION = 5;
    private int code;

    public IdempotentException() {
        super();
    }

    public IdempotentException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(Throwable cause) {
        super(cause);
    }

    public IdempotentException(int code) {
        super();
        this.code = code;
    }

    public IdempotentException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public IdempotentException(int code, String message) {
        super(message);
        this.code = code;
    }

    public IdempotentException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public boolean isBiz() {
        return code == BIZ_EXCEPTION;
    }

    public boolean isForbidded() {
        return code == FORBIDDEN_EXCEPTION;
    }

    public boolean isTimeout() {
        return code == TIMEOUT_EXCEPTION;
    }

    public boolean isNetwork() {
        return code == NETWORK_EXCEPTION;
    }

    public boolean isSerialization() {
        return code == SERIALIZATION_EXCEPTION;
    }

}
