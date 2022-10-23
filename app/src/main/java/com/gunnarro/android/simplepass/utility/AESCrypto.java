package com.gunnarro.android.simplepass.utility;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import kotlin.text.Charsets;

/*
* Use AES algorithm of 256-bit for encrypt/decrypt data.
 */
public class AESCrypto {

    private static final String SYMMETRIC_KEY_ALGORITHM = "AES";
    private static final String PASSWORD_BASED_KEY_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final Integer KEY_SIZE = 256;
    private static SecretKeySpec secretKey;


    public static String getSecretKey() {
        return secretKey != null ?  secretKey.getEncoded().toString() : null;
    }
    /**
     * initialize key
     *
     * @param encryptionKey key to be used for encryption and decryption
     */
    public static void init(String encryptionKey) throws CryptoException {
        if (secretKey == null) {
            secretKey = buildSecretKey(encryptionKey);
            Log.i("AESCrypto.init", "init with encryption key");
        }
        Log.i("AESCrypto.init", "done");
    }

    /*
     * clear initialized key
     */
    public static void reset() {
        secretKey = null;
    }

    private static SecretKeySpec buildSecretKey(final String myKey) throws CryptoException {
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, SYMMETRIC_KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            Log.e("AESCrypto.getSecretKey", "Error while getSecretKey: " + e);
            throw new CryptoException("Error while getSecretKey!", e.getCause());
        }
    }

    public static String encrypt(final String value) throws CryptoException {
        if (secretKey == null) throw new CryptoException("Not initialized, must set key.", null);
        try {
            Cipher cipher = Cipher.getInstance(PASSWORD_BASED_KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            String encrypted = Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
            Log.i("AESCrypto.encrypt", "encrypted: " + value + " -> " + encrypted);
            return encrypted;
        } catch (Exception e) {
            Log.e("AESCrypto.encrypt", "Error while encrypting: " + e.getMessage());
            throw new CryptoException("Error while encrypting!", e.getCause());
        }
    }

    public static String decrypt(final String value) throws CryptoException {
        if (secretKey == null) throw new CryptoException("Not initialized, must set key.", null);
        try {
            Cipher cipher = Cipher.getInstance(PASSWORD_BASED_KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            String decrypted = new String(cipher.doFinal(Base64.getDecoder().decode(value)));
            Log.i("AESCrypto.decrypt", "decrypted: " + value + " -> " + decrypted);
            return decrypted;
        } catch (Exception e) {
            Log.e("AESCrypto.decrypt", "Error while decrypting: " + e.getMessage());
            throw new CryptoException("Error while decrypting!", e.getCause());
        }
    }

    /**
     * generate a symmetric cryptographic key
     * Generates and returns a passphrase.
     */
    public static String generatePassphrase() throws GeneralSecurityException, IOException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(SYMMETRIC_KEY_ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        return new String(keyGenerator.generateKey().getEncoded(), Charsets.ISO_8859_1);
    }
}
