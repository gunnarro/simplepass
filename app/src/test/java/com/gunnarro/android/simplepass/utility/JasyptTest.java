package com.gunnarro.android.simplepass.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

public class JasyptTest {

    private String value = null;
    private String encryptedValue = null;

    @BeforeEach
    void setup() throws NoSuchPaddingException, NoSuchAlgorithmException {
        Jasypt.init("my-key-used-to-encrypt-and-decrypt");
        value = "my-txt-to-encrypt";
        encryptedValue = Jasypt.encrypt(value);
        Assertions.assertNotEquals(value, encryptedValue);
    }

    @Test
    void decrypt() {
        Assertions.assertEquals(value, Jasypt.decrypt(encryptedValue));
    }
}
