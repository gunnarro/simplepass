package com.gunnarro.android.simplepass.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {

    private String userName;
    private String encryptionKey;

    LoggedInUserView(String userName, String encryptionKey) {
        this.userName = userName;
        this.encryptionKey = encryptionKey;
    }

    String getEncryptionKey() {
        return encryptionKey;
    }

    String getUserName() {return userName;}
}