package com.gunnarro.android.simplepass.utility;

import android.util.Log;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.security.Provider;
import java.security.Security;

public class Jasypt {

    private final static String DEFAULT_ALGORITHM = "PBEWithMD5AndTripleDES";
    private static StandardPBEStringEncryptor encryptor;

    public static void init(String key) {
        debug();
        if (encryptor == null) {
            encryptor = new StandardPBEStringEncryptor();
            encryptor.setPassword(key);
            encryptor.setAlgorithm(DEFAULT_ALGORITHM);
        }
        Log.i("Jasypt.init", "done");
    }

    public static String encrypt(String value) {
        return encryptor.encrypt(value);
    }

    public static String decrypt(String value) {
        return encryptor.decrypt(value);
    }

    public static void reset() {
        encryptor = null;
    }

    private static void debug() {
        StringBuilder builder = new StringBuilder();
        for (Provider provider : Security.getProviders()) {
            builder.append("provider: ")
                    .append(provider.getName())
                    .append(" ")
                    .append(provider.getVersion())
                    .append("(")
                    .append(provider.getInfo())
                    .append(")\n");
        }
        String providers = builder.toString();
        Log.i("Jasypt", providers);
    }
}
