package com.gunnarro.android.simplepass.utility;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypto {

    private static final String SYMMETRIC_KEY_ALGORITHM = "AES";
    private static final String PASSWORD_BASED_KEY_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static SecretKeySpec secretKey;

    public static void init(String encryptionKey) {
        if (secretKey == null) {
            secretKey = getSecretKey(encryptionKey);
            Log.i("AESCrypto.init", "init with encryption key");
        }
    }

    private static SecretKeySpec getSecretKey(final String myKey) {
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, SYMMETRIC_KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            Log.e("AESCrypto.getSecretKey", "Error while getSecretKey: " + e);
            throw new RuntimeException("Error while getSecretKey:" + e.getMessage());
        }
    }

    public static String encrypt(final String value) {
        if (secretKey == null) throw new RuntimeException("not initialized, must set key");
        try {
            Cipher cipher = Cipher.getInstance(PASSWORD_BASED_KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            String encrypted = Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
            Log.i("AESCrypto.encrypt", "encrypted: " + value + " -> " + encrypted);
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AESCrypto.encrypt", "Error while encrypting: " + e.getMessage());
        }
        return null;
    }

    public static String decrypt(final String value) {
        if (secretKey == null) throw new RuntimeException("not initialized, must set key");
        try {
            Cipher cipher = Cipher.getInstance(PASSWORD_BASED_KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            String decrypted = new String(cipher.doFinal(Base64.getDecoder().decode(value)));
            Log.i("AESCrypto.decrypt", "decrypted: " + value + " -> " + decrypted);
            return decrypted;
        } catch (Exception e) {
            Log.e("AESCrypto.decrypt", "Error while decrypting: " + e.getMessage());
        }
        return null;
    }

}
