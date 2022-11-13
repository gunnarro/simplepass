package com.gunnarro.android.simplepass.domain.converter;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import com.gunnarro.android.simplepass.exception.CryptoException;
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
        try {
            String encrypted = AESCrypto.encrypt(value);
            Log.d("EncryptConverter.encrypt", value + " -> " + encrypted);
            return encrypted;
        } catch (CryptoException e) {
            // ignore
            return null;
        }
    }
}
