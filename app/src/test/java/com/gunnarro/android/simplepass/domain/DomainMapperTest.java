package com.gunnarro.android.simplepass.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gunnarro.android.simplepass.domain.dto.CredentialDto;
import com.gunnarro.android.simplepass.domain.entity.Credential;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class DomainMapperTest {

    @Test
    void mapToCredential() {
        CredentialDto credentialDto = new CredentialDto();
        credentialDto.setFkUserId(23L);
        credentialDto.setCreatedDate(LocalDateTime.now());
        credentialDto.setId(13L);
        credentialDto.setPassword(new EncryptedString("my-encrypted-password"));
        credentialDto.setSystem("github");
        credentialDto.setUsername("gunnarro");
        credentialDto.setLastModifiedDate(LocalDateTime.now());
        credentialDto.setPasswordStatus("STRONG");
        Credential credential = DomainMapper.mapToCredential(credentialDto);
        assertEquals(credentialDto.getFkUserId(), credential.getFkUserId());
        assertEquals(credentialDto.getId(), credential.getId());
        assertEquals(credentialDto.getCreatedDate(), credential.getCreatedDate());
        assertEquals(credentialDto.getPassword().getValue(), credential.getPassword().getValue());
        assertEquals(credentialDto.getPasswordStatus(), credential.getPasswordStatus());
        assertEquals(credentialDto.getUsername(), credential.getUsername());
        assertEquals(credentialDto.getLastModifiedDate(), credential.getLastModifiedDate());
        assertEquals(credentialDto.getSystem(), credential.getSystem());
    }

    @Test
    void mapToCredentialDto() {
        Credential credential = new Credential();
        credential.setFkUserId(23L);
        credential.setCreatedDate(LocalDateTime.now());
        credential.setId(13L);
        credential.setPassword(new EncryptedString("my-encrypted-password"));
        credential.setSystem("github");
        credential.setUsername("gunnarro");
        credential.setLastModifiedDate(LocalDateTime.now());
        credential.setPasswordStatus("STRONG");
        CredentialDto credentialDto = DomainMapper.mapToCredentialDto(credential);
        assertEquals(credential.getFkUserId(), credentialDto.getFkUserId());
        assertEquals(credential.getId(), credentialDto.getId());
        assertEquals(credential.getCreatedDate(), credentialDto.getCreatedDate());
        assertEquals(credential.getPassword().getValue(), credentialDto.getPassword().getValue());
        assertEquals(credential.getPasswordStatus(), credentialDto.getPasswordStatus());
        assertEquals(credential.getUsername(), credentialDto.getUsername());
        assertEquals(credential.getLastModifiedDate(), credentialDto.getLastModifiedDate());
        assertEquals(credential.getSystem(), credentialDto.getSystem());
    }
}
