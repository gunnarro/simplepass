package com.gunnarro.android.simplepass.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.simplepass.domain.converter.CipherConverter;

@Entity(tableName = "user")
public class User {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @TypeConverters({CipherConverter.class})
    @NonNull
    @ColumnInfo(name = "username", index = true)
    private String username;

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
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
