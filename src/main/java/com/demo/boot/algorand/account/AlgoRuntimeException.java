package com.demo.boot.algorand.account;

public class AlgoRuntimeException extends RuntimeException {

    public AlgoRuntimeException(String message) {
        super(message);
    }

    public AlgoRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlgoRuntimeException(Throwable cause) {
        super(cause);
    }

    protected AlgoRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
