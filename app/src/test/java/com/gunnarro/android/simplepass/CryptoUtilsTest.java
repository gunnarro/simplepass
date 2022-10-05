package com.gunnarro.android.simplepass;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import com.gunnarro.android.simplepass.utility.AESCrypto;
import com.gunnarro.android.simplepass.utility.CryptoException;
import com.gunnarro.android.simplepass.utility.CryptoUtils;

import java.util.logging.Logger;

/**
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CryptoUtilsTest {

    private static String encrypted;

    @Before
    public void init() throws CryptoException {
        AESCrypto.init("my-key-test");
        encrypted = AESCrypto.encrypt("my-password");
    }

    @Test
    public void decryptAES_OK() throws CryptoException {
        System.out.println("encrypted: " + encrypted);
        String decrypted = AESCrypto.decrypt(encrypted);
        assertEquals("my-password", decrypted);
    }

    @Test(expected = CryptoException.class)
    public void decryptAES_wrong_key() throws CryptoException {
        AESCrypto.reset();
        AESCrypto.init("wrong-pass");
        System.out.println("encrypted: " + encrypted);
        AESCrypto.decrypt(encrypted);
    }
}