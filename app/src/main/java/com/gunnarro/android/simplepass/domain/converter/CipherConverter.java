package com.gunnarro.android.simplepass.domain.converter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

import com.gunnarro.android.simplepass.domain.EncryptedString;
import com.gunnarro.android.simplepass.utility.AESCrypto;

import java.time.LocalDate;

/**
 * cipher used to performing encryption or decryption of entity field
 */
//@ProvidedTypeConverter
public class CipherConverter {

    private CipherConverter() {
        throw new AssertionError();
    }

    @TypeConverter
    public static EncryptedString decrypt(@Nullable String encryptedValue) {
        return new EncryptedString(AESCrypto.decrypt(encryptedValue));
    }

    @TypeConverter
    public static String encrypt(@Nullable EncryptedString value) {
        return AESCrypto.encrypt(value.getValue());
    }
}
