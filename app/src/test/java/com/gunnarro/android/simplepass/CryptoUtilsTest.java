package com.gunnarro.android.simplepass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.gunnarro.android.simplepass.ui.fragment.CredentialAddFragment;
import com.gunnarro.android.simplepass.utility.AESCrypto;
import com.gunnarro.android.simplepass.utility.CryptoException;

import org.junit.Before;
import org.junit.Test;

/**
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

    @Test
    public void hasTextRegex() {
        assertFalse(" ".matches(CredentialAddFragment.HAS_TEXT_REGEX));
        assertFalse("   ".matches(CredentialAddFragment.HAS_TEXT_REGEX));
        assertTrue("ddf".matches(CredentialAddFragment.HAS_TEXT_REGEX));
        assertTrue("d d f".matches(CredentialAddFragment.HAS_TEXT_REGEX));
    }

    @Test
    public void emptyTextRegex() {
        assertTrue(" ".matches(CredentialAddFragment.EMPTY_TEXT_REGEX));
        assertTrue("   ".matches(CredentialAddFragment.EMPTY_TEXT_REGEX));
        assertFalse("ddf".matches(CredentialAddFragment.EMPTY_TEXT_REGEX));
        assertFalse("d d f".matches(CredentialAddFragment.EMPTY_TEXT_REGEX));
        assertFalse(" d d f ".matches(CredentialAddFragment.EMPTY_TEXT_REGEX));
    }
}