package com.gunnarro.android.simplepass.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Application error. User not found!");
    }
}
