package com.gunnarro.android.simplepass.domain;

import androidx.annotation.NonNull;

public class EncryptedString {

    @NonNull
    private String value;

    public EncryptedString() {}

    public EncryptedString(String value) {
        this.value = value;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EncryptedString{");
        sb.append("value='").append(value).append('}');
        return sb.toString();
    }
}
