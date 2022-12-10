package com.gunnarro.android.simplepass.exception;

public enum ApplicationErrorCodes {

    FINGERPRINT_DISABLE_ERROR("Failed disable fingerprint login", "5001"),
    FINGERPRINT_ENABLE_ERROR("Failed enable fingerprint login", "5001");

    public final String errorMsg;
    public final String errorCode;

    ApplicationErrorCodes(String errorMsg, String errorCode) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }
}
