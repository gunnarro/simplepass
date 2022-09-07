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

    private boolean isDataValid;

    LoginFormState(@Nullable Integer usernameError, @Nullable Integer encryptionKeyError) {
        this.usernameError = usernameError;
        this.encryptionKeyError = encryptionKeyError;
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

    boolean isDataValid() {
        return isDataValid;
    }
}