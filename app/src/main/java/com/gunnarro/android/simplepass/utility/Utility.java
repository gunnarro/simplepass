package com.gunnarro.android.simplepass.utility;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Utility {

    private static final SimpleDateFormat dateFormatter;
    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm";
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private static String currentUUID;

    static {
        dateFormatter = new SimpleDateFormat(DATE_TIME_PATTERN, Locale.getDefault());
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private Utility() {
        genNewUUID();
    }

    public static ObjectMapper getJsonMapper() {
        return mapper;
    }

    public static void genNewUUID() {
        currentUUID = java.util.UUID.randomUUID().toString();
    }

    public static String buildTag(Class<?> clazz, String tagName) {
        return new StringBuilder(clazz.getSimpleName())
                .append(".").append(tagName)
                .append(", UUID=").append(currentUUID)
                .append(", thread=").append(Thread.currentThread().getName())
                .toString();
    }

    public static String formatDateTime(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return localDateTime.format(dateTimeFormatter);
        }
        return null;
    }

    public static LocalDateTime toLocalDateTime(String dateTimeStr) {
        Log.d("Utility", "toLocalDateTime: " + dateTimeStr);
        if (dateTimeStr == null || dateTimeStr.length() != DATE_TIME_PATTERN.length()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Locale.getDefault()));
    }

    private static Pattern positiveIntegerPattern = Pattern.compile("[0-9]+");
    public static boolean isInteger(String value) {
        if (value == null) {
            return false;
        }
        return positiveIntegerPattern.matcher(value).matches();
    }
}