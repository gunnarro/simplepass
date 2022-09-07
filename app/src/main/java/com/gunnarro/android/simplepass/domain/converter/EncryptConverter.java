package com.gunnarro.android.simplepass.domain.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import com.gunnarro.android.simplepass.domain.EncryptedString;
import com.gunnarro.android.simplepass.utility.AESCrypto;

/**
 * Converter used to performing encryption of String
 */
public class EncryptConverter {

    private EncryptConverter() {
        throw new AssertionError();
    }

    @TypeConverter
    public static String encrypt(@Nullable String value) {
        return AESCrypto.encrypt(value);
    }
}
