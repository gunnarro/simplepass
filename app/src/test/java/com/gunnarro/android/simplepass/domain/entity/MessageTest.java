package com.gunnarro.android.simplepass.domain.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageTest {
    @Test
    void defaultValues() {
        Message message = new Message();
        Assertions.assertNull(message.getId());
        Assertions.assertNull(message.getCreatedDate());
        Assertions.assertNull(message.getLastModifiedDate());
        Assertions.assertNotNull(message.toString());
    }
}
