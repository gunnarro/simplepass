package com.gunnarro.android.simplepass.ui.view;

import androidx.annotation.Nullable;

public class CredentialFormState {

    @Nullable
    private Integer systemNameError;

    @Nullable
    private Integer userNameError;

    @Nullable
    private String systemNameErrorMsg;

    @Nullable
    private String userNameErrorMsg;

    private boolean isDataValid;

    CredentialFormState(@Nullable Integer systemNameError, @Nullable Integer userNameError, @Nullable String systemNameErrorMsg, @Nullable String userNameErrorMsg) {
        this.systemNameError = systemNameError;
        this.userNameError = userNameError;
        this.systemNameErrorMsg = systemNameErrorMsg;
        this.userNameErrorMsg = userNameErrorMsg;
        this.isDataValid = false;
    }

    CredentialFormState(boolean isDataValid) {
        this.systemNameError = null;
        this.userNameError = null;
        this.isDataValid = isDataValid;
    }



    boolean isDataValid() {
        return isDataValid;
    }
}
