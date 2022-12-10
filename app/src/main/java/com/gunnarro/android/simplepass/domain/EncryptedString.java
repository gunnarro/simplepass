package com.gunnarro.android.simplepass.domain;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EncryptedString {

    @NonNull
    private final String value;

    @JsonCreator
    public EncryptedString(@NonNull @JsonProperty("value") String value) {
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
