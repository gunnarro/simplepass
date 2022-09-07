package com.gunnarro.android.simplepass.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.simplepass.domain.EncryptedString;
import com.gunnarro.android.simplepass.domain.converter.CipherConverter;

import java.util.Objects;

@TypeConverters({CipherConverter.class})
@Entity(tableName = "credential")
public class Credential {

    @PrimaryKey(autoGenerate = true)
    private Long id;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credential that = (Credential) o;
        return system.equals(that.system) && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(system, username);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Credential{");
        sb.append("id=").append(id);
        sb.append(", system='").append(system).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
