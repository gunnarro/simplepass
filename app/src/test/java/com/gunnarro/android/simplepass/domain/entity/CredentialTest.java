package com.gunnarro.android.simplepass.domain.entity;

import com.gunnarro.android.simplepass.domain.EncryptedString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class CredentialTest {

    @Test
    void checkPassword(){
        Credential credential = new Credential();
        credential.setPassword(new EncryptedString("encrypted-password"));
        Assertions.assertEquals("encrypted-password", credential.getPassword().getValue());
    }

    @Test
    void defaultValues() {
        Credential credential = new Credential();
        Assertions.assertNull(credential.getId());
        Assertions.assertNull(credential.getCreatedDate());
        Assertions.assertNull(credential.getLastModifiedDate());
        Assertions.assertNotNull(credential.toString());
    }
}
