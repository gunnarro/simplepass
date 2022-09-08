package com.gunnarro.android.simplepass;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import com.gunnarro.android.simplepass.utility.AESCrypto;
import com.gunnarro.android.simplepass.utility.CryptoUtils;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@Ignore
public class CryptoUtilsTest {

    private static String encrypted;

    @Before
    public void init() {
        AESCrypto.init("my-key-test");
        encrypted = AESCrypto.encrypt("my-password");
    }

    @Test
    public void encrypt() {
        String password = "value to encrypt";
     //   String encrypted = CryptoUtils.encrypt( password);
     //   String decrypted = CryptoUtils.decrypt( encrypted);
     //   assertEquals(password, decrypted);
    }

    @Test
    public void encryptAES() {
        //encrypted = AESCrypto.encrypt("my-password", "my-secret-key");
        //assertEquals("my-password", decrypted);
        System.out.println("encrypted: " + encrypted);
    }

    @Test
    public void decryptAES_OK() {
        System.out.println("encrypted: " + encrypted);
        String decrypted = AESCrypto.decrypt(encrypted);
        assertEquals("my-password", decrypted);
    }

    @Test
    public void decryptAES_wrong_key() {
        System.out.println("encrypted: " + encrypted);
        String decrypted = AESCrypto.decrypt(encrypted);
        assertNull(decrypted);
    }
}