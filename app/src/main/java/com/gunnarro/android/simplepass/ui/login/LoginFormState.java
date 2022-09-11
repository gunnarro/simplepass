package com.gunnarro.android.simplepass.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer encryptionKeyError;

    @Nullable
    private String encryptionKeyErrorMsg;

    private boolean isDataValid;

    LoginFormState(@Nullable Integer usernameError, @Nullable String encryptionKeyErrorMsg) {
        this.usernameError = usernameError;
        this.encryptionKeyErrorMsg = encryptionKeyErrorMsg;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.encryptionKeyError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getEncryptionKeyError() {
        return encryptionKeyError;
    }

    @Nullable
    String getEncryptionKeyErrorMsg() {
        return encryptionKeyErrorMsg;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}