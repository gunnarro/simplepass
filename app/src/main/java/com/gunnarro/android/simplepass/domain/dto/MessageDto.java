package com.gunnarro.android.simplepass.domain.dto;

import java.time.LocalDateTime;

public class MessageDto {

    private Long id;

    private Long fkUserId;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private String tag;

    private String content;
}
