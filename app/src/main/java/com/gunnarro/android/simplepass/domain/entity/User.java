package com.gunnarro.android.simplepass.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.simplepass.domain.converter.EncryptConverter;
import com.gunnarro.android.simplepass.domain.converter.LocalDateTimeConverter;

import java.time.LocalDateTime;
import java.util.Objects;

@TypeConverters({EncryptConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "user", indices = {@Index(value = {"username"}, unique = true)})
public class User {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;

    @NonNull
    @ColumnInfo(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @NonNull
    @ColumnInfo(name = "last_login_date")
    private LocalDateTime lastLoginDate = LocalDateTime.now();

    @NonNull
    @ColumnInfo(name = "username")
    private String username;

    @NonNull
    @ColumnInfo(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    public User() {
    }

    @Ignore
    public User(@NonNull String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(@NonNull Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    @NonNull
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@NonNull LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @NonNull
    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(@NonNull LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", lastLoginDate=").append(lastLoginDate);
        sb.append(", username='").append(username).append('\'');
        sb.append(", failedLoginAttempts=").append(failedLoginAttempts);
        sb.append('}');
        return sb.toString();
    }
}
