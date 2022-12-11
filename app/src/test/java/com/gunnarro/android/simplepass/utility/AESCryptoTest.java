package com.gunnarro.android.simplepass.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gunnarro.android.simplepass.exception.CryptoException;
import com.gunnarro.android.simplepass.ui.fragment.CredentialAddFragment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
class AESCryptoTest {

    private static String encrypted;

    @BeforeEach
    void init() throws CryptoException {
        AESCrypto.init("my-encryption-key-ie-master-password");
        encrypted = AESCrypto.encrypt("my-password");
    }

    @Test
    void decryptAES_OK() throws CryptoException {
        System.out.println("encrypted: " + encrypted);
        String decrypted = AESCrypto.decrypt(encrypted);
        assertEquals("my-password", decrypted);
    }

    @Test
    void decryptAES_wrong_key() throws CryptoException {
        AESCrypto.reset();
        AESCrypto.init("wrong-pass");
        System.out.println("encrypted: " + encrypted);
        CryptoException ex = assertThrows(CryptoException.class, () -> AESCrypto.decrypt(encrypted));
        assertEquals("Error while decrypting!", ex.getMessage());
    }

    @Test
    void emptyTextRegex() {
        assertTrue(" ".matches(CredentialAddFragment.EMPTY_TEXT_REGEX));
        assertTrue("   ".matches(CredentialAddFragment.EMPTY_TEXT_REGEX));
        assertFalse("ddf".matches(CredentialAddFragment.EMPTY_TEXT_REGEX));
        assertFalse("d d f".matches(CredentialAddFragment.EMPTY_TEXT_REGEX));
        assertFalse(" d d f ".matches(CredentialAddFragment.EMPTY_TEXT_REGEX));
    }

    @Test
    void hasTextRegex() {
        assertFalse(" ".matches(CredentialAddFragment.HAS_TEXT_REGEX));
        assertFalse("   ".matches(CredentialAddFragment.HAS_TEXT_REGEX));
        assertTrue("ddf".matches(CredentialAddFragment.HAS_TEXT_REGEX));
        assertTrue("d d f".matches(CredentialAddFragment.HAS_TEXT_REGEX));
    }

    @Test
    void getSecretKey() throws CryptoException {
        assertNotNull(AESCrypto.generatePassphrase());
    }
}
