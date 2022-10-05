package com.gunnarro.android.simplepass.domain.converter;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

import com.gunnarro.android.simplepass.domain.EncryptedString;
import com.gunnarro.android.simplepass.utility.AESCrypto;
import com.gunnarro.android.simplepass.utility.CryptoException;

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
            String encrypted = AESCrypto.encrypt(value.toString());
            Log.d("EncryptConverter.encrypt", value + " -> " + encrypted);
            return encrypted;
        } catch (CryptoException e) {
            // ignore
            return null;
        }
    }
}
