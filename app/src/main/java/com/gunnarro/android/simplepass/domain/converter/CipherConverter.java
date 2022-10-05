package com.gunnarro.android.simplepass.domain.converter;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import com.gunnarro.android.simplepass.domain.EncryptedString;
import com.gunnarro.android.simplepass.utility.AESCrypto;
import com.gunnarro.android.simplepass.utility.CryptoException;

/**
 * TypeConverter are methods that tell Room database how to convert custom types to and from kinds that Room understands.
 *
 * cipher used to performing encryption or decryption of entity field
 */
//@ProvidedTypeConverter
public class CipherConverter {

    private CipherConverter() {
        throw new AssertionError();
    }

    @TypeConverter
    public static String encrypt(@Nullable EncryptedString value) {
        try {
            String encrypted = AESCrypto.encrypt(value.getValue());
            Log.d("CipherConverter.encrypt", value.getValue() + " -> " + encrypted);
            return encrypted;
        } catch (CryptoException e) {
            // ignore, value is not decrypted likely because of wrong key
            return null;
        }
    }

    @TypeConverter
    public static EncryptedString decrypt(@Nullable String encryptedValue) {
        try {
            EncryptedString decrypted = new EncryptedString(AESCrypto.decrypt(encryptedValue));
            Log.d("CipherConverter.decrypt", encryptedValue + " -> " + decrypted.getValue());
            return decrypted;
        } catch (CryptoException e) {
            // ignore, value is not encrypted likely because of wrong key
            // fixme only for check encryption, should return null
            return new EncryptedString(encryptedValue);
        }
    }
}
