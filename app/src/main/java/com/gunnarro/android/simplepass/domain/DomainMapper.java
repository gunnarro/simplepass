package com.gunnarro.android.simplepass.domain;

import com.gunnarro.android.simplepass.domain.dto.CredentialDto;
import com.gunnarro.android.simplepass.domain.entity.Credential;

public class DomainMapper {

    public static Credential mapToCredential(CredentialDto credentialDto) {
        Credential credential = new Credential();
        credential.setCreatedDate(credentialDto.getCreatedDate());
        credential.setPassword(credentialDto.getPassword());
        credential.setLastModifiedDate(credentialDto.getLastModifiedDate());
        credential.setSystem(credentialDto.getSystem());
        credential.setUsername(credentialDto.getUsername());
        credential.setFkUserId(credentialDto.getFkUserId());
        credential.setId(credentialDto.getId());
        credential.setPasswordStatus(credentialDto.getPasswordStatus());
        return credential;
    }

    public static CredentialDto mapToCredentialDto(Credential credential) {
        CredentialDto credentialDto = new CredentialDto();
        credentialDto.setCreatedDate(credential.getCreatedDate());
        credentialDto.setPassword(credential.getPassword());
        credentialDto.setLastModifiedDate(credential.getLastModifiedDate());
        credentialDto.setSystem(credential.getSystem());
        credentialDto.setUsername(credential.getUsername());
        credentialDto.setFkUserId(credential.getFkUserId());
        credentialDto.setId(credential.getId());
        credentialDto.setPasswordStatus(credential.getPasswordStatus());
        return credentialDto;
    }
}
