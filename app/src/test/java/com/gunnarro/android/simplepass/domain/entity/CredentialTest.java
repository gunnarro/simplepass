package com.gunnarro.android.simplepass.domain.entity;

import static org.junit.Assert.assertEquals;

import com.gunnarro.android.simplepass.domain.EncryptedString;

import org.junit.Test;

public class CredentialTest {

    @Test
    public void checkPassword(){
        Credential credential = new Credential();
        credential.setPassword(new EncryptedString("encrypted-password"));
        assertEquals("encrypted-password", credential.getPassword().getValue());
    }
}
