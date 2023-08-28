package com.gunnarro.android.simplepass.utility;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class Utility {

    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm";
    //  private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private static String currentUUID;

    public static DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    private static final Gson gson = new GsonBuilder()
            //   .registerTypeAdapter(Id.class, new IdTypeAdapter())
            // .setDateFormat(DateFormat.LONG)
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .setDateFormat("dd-MM-yyyy HH:mm:ss.SSS")
            // .setVersion(1.0)
            .create();

    private Utility() {
        genNewUUID();
    }


    public static Gson gsonMapper() {
        return gson;
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
        return LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
    }

    private static final Pattern positiveIntegerPattern = Pattern.compile("\\d+");

    public static boolean isInteger(String value) {
        if (value == null) {
            return false;
        }
        return positiveIntegerPattern.matcher(value).matches();
    }
}