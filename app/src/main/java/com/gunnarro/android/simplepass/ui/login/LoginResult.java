package com.gunnarro.android.simplepass.ui.login;

import androidx.annotation.Nullable;

import com.gunnarro.android.simplepass.domain.dto.LoggedInUserDto;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private LoggedInUserDto loggedInUserDto;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable LoggedInUserDto loggedInUserDto) {
        this.loggedInUserDto = loggedInUserDto;
    }

    @Nullable
    LoggedInUserDto getLoggedInUseDto() {
        return loggedInUserDto;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}