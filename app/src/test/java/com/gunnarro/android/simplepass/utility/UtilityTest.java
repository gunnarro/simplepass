package com.gunnarro.android.simplepass.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class UtilityTest {

    @Test
    public void toLocalDateTime() {
        assertEquals("2022-11-11T23:32", Utility.toLocalDateTime("11-11-2022 23:32").toString());
    }

    @Test
    public void formatDateTime() {
        assertEquals("11-11-2022 23:32", Utility.formatDateTime(LocalDateTime.now().withYear(2022).withMonth(11).withDayOfMonth(11).withHour(23).withMinute(32).withSecond(23)));
    }
}
