package com.gunnarro.android.simplepass.domain;

import androidx.annotation.NonNull;

public class EncryptedString {

    @NonNull
    private String value;

    /**
     * Needed by json converter
     */
    public EncryptedString() {}

    public EncryptedString(@NonNull String value) {
        this.value = value;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EncryptedString{");
        sb.append("value='").append(value).append('}');
        return sb.toString();
    }
}
