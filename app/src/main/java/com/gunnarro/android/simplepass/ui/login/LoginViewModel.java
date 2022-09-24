package com.gunnarro.android.simplepass.ui.login;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.domain.entity.User;
import com.gunnarro.android.simplepass.repository.UserRepository;
import com.gunnarro.android.simplepass.utility.AESCrypto;
import com.gunnarro.android.simplepass.validator.CustomPasswordValidator;

import java.util.List;

public class LoginViewModel extends AndroidViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();

    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String encryptionKey) {
        // Must init the AESCrypto here upon each login attempt.
        AESCrypto.init(encryptionKey);
        // can be launched in a separate asynchronous job
        try {
            User loginUser = new User(username);
            List<User> users = userRepository.getUsers();
            Log.d("LoginViewModel.login", "got users: " + users);
            if (users == null || users.isEmpty()) {
                Log.d("LoginViewModel.login", "first time login! create user. " + loginUser);
                userRepository.insert(loginUser);
                loginResult.setValue(new LoginResult(new LoggedInUserView(username, null)));
            } else if (users.stream().anyMatch(u -> u.getUsername().equals(loginUser.getUsername()))) {
                // If hit, we do know that the encryption key is correct, so let user pass.
                Log.d("LoginViewModel.login", "user have access " + loginUser);
                loginResult.setValue(new LoginResult(new LoggedInUserView(username, null)));
            } else {
                Log.d("LoginViewModel.login", "access denied for user, " + loginUser);
                // access denied for this user and encryption key combination
                loginResult.setValue(new LoginResult(R.string.login_user_access_denied));
            }
        } catch (Exception e) {
            e.printStackTrace();
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String userName, String encryptionKey) {
        List list = isEncryptionKeyValid(encryptionKey);
        if (!isUsernameValid(userName)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!list.isEmpty()) {
            loginFormState.setValue(new LoginFormState(null, list.toString()));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder encryptionKey validation check
    private List<String> isEncryptionKeyValid(String encryptionKey) {
        return CustomPasswordValidator.passwordStrength(encryptionKey);
    }

    private boolean isUsernameValid(String username) {
        return username != null && username.trim().length() > 1;
    }
}