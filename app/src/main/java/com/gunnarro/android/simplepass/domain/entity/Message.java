package com.gunnarro.android.simplepass.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.simplepass.domain.converter.CipherConverter;
import com.gunnarro.android.simplepass.domain.converter.LocalDateTimeConverter;

import java.time.LocalDateTime;
import java.util.Objects;

@TypeConverters({CipherConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "message",
        indices = {@Index(value = {"fk_user_id", "tag"}, unique = true)},
        foreignKeys = {@ForeignKey(entity = User.class, parentColumns = "id", childColumns = "fk_user_id")})
public class Message {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", index = true)
    private Long id;

    @NonNull
    @ColumnInfo(name = "fk_user_id", index = true)
    private Long fkUserId;

    @NonNull
    @ColumnInfo(name = "created_date")
    private LocalDateTime createdDate;

    @NonNull
    @ColumnInfo(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @NonNull
    @ColumnInfo(name = "tag", index = true)
    private String tag;

    @ColumnInfo(name = "content")
    private String content;

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
    public String getTag() {
        return tag;
    }

    public void setTag(@NonNull String tag) {
        this.tag = tag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return fkUserId.equals(message.fkUserId) && tag.equals(message.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fkUserId, tag);
    }
}
