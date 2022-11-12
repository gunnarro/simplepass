package com.gunnarro.android.simplepass.ui.login;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.dto.LoggedInUserDto;
import com.gunnarro.android.simplepass.domain.entity.User;
import com.gunnarro.android.simplepass.repository.UserRepository;
import com.gunnarro.android.simplepass.utility.AESCrypto;
import com.gunnarro.android.simplepass.validator.CustomPasswordValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class LoginViewModel extends AndroidViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();

    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) throws GeneralSecurityException, IOException {
        super(application);
        userRepository = new UserRepository(AppDatabase.getDatabaseEncrypted(application).userDao());
        Log.i("LoginViewModel", "initialized");
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public boolean isFirstTimeLogin() throws Exception {
        return userRepository.isFirstTimeLogin();
    }

    /**
     * Account must exist before fingerprint login are available
     */
    public void loginFingerprint() throws Exception {
        AESCrypto.reset();
        AESCrypto.init(AppDatabase.getEncryptionMasterPass(getApplication().getApplicationContext()));
        List<User> users = userRepository.getUsers();
        User user = users.stream().findFirst().orElse(null);
        Log.d("LoginViewModel.loginFingerprint", "login user OK: " + user);
        loginResult.setValue(new LoginResult(new LoggedInUserDto(user.getId(), user.getUsername())));
    }

    public void login(String username, String encryptionKey, boolean isFingerprintLoginEnabled) {
        try {
            // Must first reset and then init the AESCrypto here upon each login attempt.
            AESCrypto.reset();
            AESCrypto.init(encryptionKey);
            User encryptedLoginUser = new User(AESCrypto.encrypt(username));
            List<User> users = userRepository.getUsers();
            User user = users.stream().filter(u -> u.getUsername().equals(encryptedLoginUser.getUsername())).findAny().orElse(null);
            Log.d("LoginViewModel.login", "got users: " + users);
            if (user == null && users.isEmpty()) {
                Log.d("LoginViewModel.login", "first time login! create user: " + username);
                userRepository.insert(encryptedLoginUser);
                loginResult.setValue(new LoginResult(new LoggedInUserDto(1L, username)));
                // check and save key if fingerprint login is enabled
                enableFingerprintLogin(isFingerprintLoginEnabled, encryptionKey);
            } else if (user != null ) {
                // If hit, we do know that the encryption key is correct, so let user pass.
                Log.d("LoginViewModel.login", "user have access: " + encryptedLoginUser);
                loginResult.setValue(new LoginResult(new LoggedInUserDto(user.getId(), username)));
                // check and save key if fingerprint login is enabled
                enableFingerprintLogin(isFingerprintLoginEnabled, encryptionKey);
            } else {
                Log.d("LoginViewModel.login", "access denied for user, " + encryptedLoginUser + "(" + username + ")");
                // FIXME will never hit because username is decrypted with wrong password
                userRepository.updateFailedLoginAttempts(encryptedLoginUser.getUsername());
                // access denied for this user and encryption key combination
                loginResult.setValue(new LoginResult(R.string.login_user_access_denied));
            }
        } catch (Exception e) {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String userName, String encryptionKey) {
        List<String> list = isEncryptionKeyValid(encryptionKey);
        if (!isUsernameValid(userName)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!list.isEmpty()) {
            loginFormState.setValue(new LoginFormState(null, list.toString()));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
        Log.d("loginDataChanged", loginFormState.toString());
    }

    private List<String> isEncryptionKeyValid(String encryptionKey) {
        return new CustomPasswordValidator().passwordStrength(encryptionKey);
    }

    private boolean isUsernameValid(String username) {
        return username != null && username.trim().length() > 1;
    }

    private void enableFingerprintLogin(boolean isEnableFingerprintLogin, String masterPass) {
        if (isEnableFingerprintLogin) {
            // save master password in encrypted shared preferences for use when fingerprint login
            try {
                AppDatabase.enableFingerprintLogin(getApplication().getApplicationContext(), masterPass);
            } catch (Exception e) {
                // something failed during saving master password, show an alert that fingerprint login could not be activated
                Toast.makeText(getApplication().getApplicationContext(), "Fingerprint login could not be activated! Please report error! Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}