package com.gunnarro.android.simplepass.utility;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class Utility {

    public static final String WORKDAY_DATE_PATTERN = "EEE d MMM";
    private static final String LOG_TAG = Utility.class.getSimpleName();
    private static final SimpleDateFormat dateFormatter;
    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm";
    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final String TIME_PATTERN = "HH:mm";
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

    public static Date toDate(String dateStr) {
        try {
            return dateFormatter.parse(dateStr);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    public static String formatTime(long timeMs) {
        if (timeMs >= 0) {
            return dateFormatter.format(new Date(timeMs));
        }
        return "";
    }

    public static String formatDateTime(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return localDateTime.format(dateTimeFormatter);
        } else {
            return null;
        }
    }

    public static DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.getDefault());
    }

    public static LocalDate toLocalDate(String dateStr) {
        Log.d("Utility", "toLocalDate: " + dateStr);
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.getDefault()));
    }

    public static LocalDateTime toLocalDateTime(String dateTimeStr) {
        Log.d("Utility", "toLocalDateTime: " + dateTimeStr);
        if (dateTimeStr == null || dateTimeStr.length() != DATE_TIME_PATTERN.length()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Locale.getDefault()));
    }

    public static LocalTime toLocalTime(String timeStr) {
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern(TIME_PATTERN, Locale.getDefault()));
    }

    public static String formatTime(LocalTime localTime) {
        return localTime != null ? localTime.format(DateTimeFormatter.ofPattern(TIME_PATTERN, Locale.getDefault())) : null;
    }

    public static String formatDate(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    public static String getDateDiffInHours(LocalTime d1, LocalTime d2) {
        if (d1 != null && d2 != null) {
            int diffHour = d2.getHour() - d1.getHour();
            int diffMinute = d2.getMinute() - d1.getMinute();
            return String.format("%s.%s", diffHour, new DecimalFormat("#").format((double) diffMinute / 60 * 10));
        }
        return null;
    }

    public static String formatToHHMM(int hour, int minute) {
        String hh = String.format("%s", hour);
        String mm = String.format("%s", minute);
        if (hour >= 0 && hour < 10) {
            hh = String.format("0%s", hour);
        }
        if (minute >= 0 && minute < 10) {
            mm = String.format("0%s", minute);
        }
        return String.format("%s:%s", hh, mm);
    }

    public static String formatToDDMMYYYY(int year, int month, int day) {
        String dd = String.format("%s", day);
        String mm = String.format("%s", month);
        if (day >= 0 && day < 10) {
            dd = String.format("0%s", day);
        }
        if (month >= 0 && month < 10) {
            mm = String.format("0%s", month);
        }
        return String.format("%s-%s-%s", dd, mm, year);
    }

    /**
     * @param map the map of words to be sorted
     * @return return a sorted map by value, i.e most frequent word at top
     */
    public static LinkedHashMap<String, Integer> getTop10Values(Map<String, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}