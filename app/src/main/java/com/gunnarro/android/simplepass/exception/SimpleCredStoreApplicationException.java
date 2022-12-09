package com.gunnarro.android.simplepass.exception;

public class SimpleCredStoreApplicationException extends RuntimeException {
    private static final String DEFAULT_MESSAGE_FORMAT = "Application error! Please Report error to app developer. Error=%s";
    private final String errorCode;

    public SimpleCredStoreApplicationException(String msg, String errorCode, Throwable throwable) {
        super(String.format(String.format(DEFAULT_MESSAGE_FORMAT, msg), errorCode), throwable);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
