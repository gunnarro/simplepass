package com.gunnarro.android.simplepass.domain.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeConverter {

    private LocalDateTimeConverter() {
        throw new AssertionError();
    }

    @TypeConverter
    public static LocalDateTime toLocalDateTime(@Nullable Long epoch) {
        return epoch == null ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.systemDefault());
    }

    @TypeConverter
    public static Long fromLocalDateTime(@Nullable LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }
}
