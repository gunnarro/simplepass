package com.gunnarro.android.simplepass.exception;

public class SimpleCredStoreApplicationException extends RuntimeException {
    private final static String DEFAULT_MESSAGE_FORMAT = "Application error! Please Report error to app developer. Error=%s";
    String errorCode;

    public SimpleCredStoreApplicationException(String msg, String errorCode, Throwable throwable) {
        super(String.format(String.format(DEFAULT_MESSAGE_FORMAT, msg), errorCode), throwable);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
