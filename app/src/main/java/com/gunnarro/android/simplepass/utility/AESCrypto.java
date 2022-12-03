package com.gunnarro.android.simplepass.utility;

import android.util.Log;

import com.gunnarro.android.simplepass.exception.CryptoException;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import kotlin.text.Charsets;

/*
 *
 * Java AES Encryption can be used for various types of data including strings, files, objects. It can also be used for password-protected data.
 *
 * Advanced Encryption Standard (AES) is the successor of the Data Encryption Standard(DES), published in 2001 by the National Institute of Standards and Technology(NIST).
 * It's classified as a symmetric block cipher.
 * AES keys are symmetric keys that can be three different key lengths (128, 192, or 256 bits).
 * We use AES algorithm of 256-bit for encrypt/decrypt data.
 *
 *  see https://www.javainterviewpoint.com/java-aes-256-gcm-encryption-and-decryption/
 *
 * The GCM (Galois Counter Mode) mode which works internally with zero/no padding scheme,
 * is recommended (AES/GCM/NoPadding), as it is designed to provide both data authenticity (integrity) and confidentiality.
 */
public class AESCrypto {

    public static final int GCM_IV_LENGTH = 128;
    public static final int GCM_TAG_LENGTH = 128;
    private static final String SYMMETRIC_KEY_ALGORITHM = "AES";
    private static final String MESSAGE_DIGEST = "SHA-256";
    // - The first part is the name of the algorithm – AES
    // - The second part is the mode in which the algorithm should be used – GCM
    // - The third part is the padding scheme which is going to be used – NoPadding. Since GCM Mode transforms block encryption into stream encryption
    private static final String AES_CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    // Possible AES KEY_SIZE values are 128, 192 and 256
    private static final Integer AES_KEY_SIZE = 256;
    // holds the key used to encrypt and decrypt
    private static SecretKeySpec secretKey;

    public static String getSecretKey() {
        return secretKey != null ? Arrays.toString(secretKey.getEncoded()) : null;
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
            MessageDigest sha = MessageDigest.getInstance(MESSAGE_DIGEST);
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, SYMMETRIC_KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            Log.e("AESCrypto.getSecretKey", "Error while getSecretKey: " + e);
            throw new CryptoException("Error while getSecretKey!", e.getCause());
        }
    }

    private static GCMParameterSpec getGCMParameterSpec(byte[] key) {
        byte[] iv = Arrays.copyOf(key, GCM_IV_LENGTH);
        return new GCMParameterSpec(GCM_TAG_LENGTH, iv);
    }

    public static String encrypt(final String value) throws CryptoException {
        if (secretKey == null) throw new CryptoException("Not initialized, must set key.", null);
        try {
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
            // Initialize Cipher for ENCRYPT_MODE
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, getGCMParameterSpec(secretKey.getEncoded()));
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
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, getGCMParameterSpec(secretKey.getEncoded()));
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
    public static String generatePassphrase() throws CryptoException {
        return Arrays.toString(buildSecretKey(UUID.randomUUID().toString()).getEncoded());
      //  KeyGenerator keyGenerator = KeyGenerator.getInstance(SYMMETRIC_KEY_ALGORITHM);
      //  keyGenerator.init(AES_KEY_SIZE);
      //  return new String(keyGenerator.generateKey().getEncoded(), Charsets.ISO_8859_1);
    }

}
