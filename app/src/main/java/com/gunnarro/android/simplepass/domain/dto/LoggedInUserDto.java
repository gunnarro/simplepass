package com.gunnarro.android.simplepass.domain.dto;

public class LoggedInUserDto {

    private Long id;
    private String userName;

    public LoggedInUserDto(Long id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public Long getId() {
        return id;
    }
}