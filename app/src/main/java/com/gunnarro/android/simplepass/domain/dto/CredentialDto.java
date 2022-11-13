package com.gunnarro.android.simplepass.domain.dto;

import androidx.annotation.NonNull;

import com.gunnarro.android.simplepass.domain.EncryptedString;

import java.time.LocalDateTime;

public class CredentialDto {

    private Long id;

    private Long fkUserId;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private String system;

    private String username;

    private EncryptedString password;

    private String passwordStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Long fkUserId) {
        this.fkUserId = fkUserId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public EncryptedString getPassword() {
        return password;
    }

    public void setPassword(EncryptedString password) {
        this.password = password;
    }

    public String getPasswordStatus() {
        return passwordStatus;
    }

    public void setPasswordStatus(String passwordStatus) {
        this.passwordStatus = passwordStatus;
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CredentialDto{");
        sb.append("id=").append(id);
        sb.append(", fkUserId=").append(fkUserId);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", lastModifiedDate=").append(lastModifiedDate);
        sb.append(", system='").append(system).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password=").append(password);
        sb.append(", passwordStatus='").append(passwordStatus).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
