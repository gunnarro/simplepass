package com.gunnarro.android.simplepass.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.simplepass.domain.EncryptedString;
import com.gunnarro.android.simplepass.domain.converter.CipherConverter;
import com.gunnarro.android.simplepass.domain.converter.LocalDateTimeConverter;

import java.time.LocalDateTime;
import java.util.Objects;

@TypeConverters({CipherConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "credential",
        indices = {@Index(value = {"fk_user_id", "system", "username"}, unique = true)},
        foreignKeys = {@ForeignKey(entity = User.class, parentColumns = "id", childColumns = "fk_user_id")})
public class Credential {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", index = true)
    private Long id;

    @NonNull
    @ColumnInfo(name = "fk_user_id", index = true)
    private Long fkUserId;

    @NonNull
    @ColumnInfo(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @NonNull
    @ColumnInfo(name = "last_modified_date")
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @NonNull
    @ColumnInfo(name = "system", index = true)
    private String system;

    @ColumnInfo(name = "url", index = false)
    private String url;

    @NonNull
    @ColumnInfo(name = "username", index = true)
    private String username;

    @NonNull
    @ColumnInfo(name = "password", index = true)
    private EncryptedString password;

    @ColumnInfo(name = "password_status", index = true)
    private String passwordStatus;

    /**
     * default constructor, Room accepts only one
     */
    public Credential() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public Long getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(@NonNull Long fkUserId) {
        this.fkUserId = fkUserId;
    }


    @NonNull
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@NonNull LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @NonNull
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(@NonNull LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @NonNull
    public String getSystem() {
        return system;
    }

    public void setSystem(@NonNull String system) {
        this.system = system;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public EncryptedString getPassword() {
        return password;
    }

    public void setPassword(@NonNull EncryptedString password) {
        this.password = password;
    }

    public String getPasswordStatus() {
        return passwordStatus;
    }

    public void setPasswordStatus(String passwordStatus) {
        this.passwordStatus = passwordStatus;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credential that = (Credential) o;
        return fkUserId.equals(that.fkUserId) && system.equals(that.system) && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fkUserId, system, username);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Credential{");
        sb.append("id=").append(id);
        sb.append(", fkUserId=").append(fkUserId);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", lastModifiedDate=").append(lastModifiedDate);
        sb.append(", system='").append(system).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password=").append(password);
        sb.append(", passwordStatus='").append(passwordStatus).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
