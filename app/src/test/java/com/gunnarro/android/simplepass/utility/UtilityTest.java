package com.gunnarro.android.simplepass.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.domain.entity.Message;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class UtilityTest {

    @Test
    void toLocalDateTime() {
        assertEquals("2022-11-11T23:32", Utility.toLocalDateTime("11-11-2022 23:32").toString());
    }

    @Test
    void formatDateTime() {
        assertEquals("11-11-2022 23:32", Utility.formatDateTime(LocalDateTime.now().withYear(2022).withMonth(11).withDayOfMonth(11).withHour(23).withMinute(32).withSecond(23)));
    }

    @Test
    void credentialToJson() {
        Credential credential = new Credential();
        credential.setLastModifiedDate(LocalDateTime.of(2023, 12, 12, 12, 12, 12));
        assertEquals("{  \"LastModifiedDate\": \"12-12-2023 12:12\"}", Utility.gsonMapper().toJson(credential).replace("\n", "").trim());
    }

    @Test
    void messageToJson() {
        assertEquals("{}", Utility.gsonMapper().toJson(new Message()));
    }
}
