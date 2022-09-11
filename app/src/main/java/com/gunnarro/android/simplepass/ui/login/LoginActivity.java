package com.gunnarro.android.simplepass.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.gunnarro.android.simplepass.databinding.ActivityLoginBinding;
import com.gunnarro.android.simplepass.ui.MainActivity;
import com.gunnarro.android.simplepass.validator.CustomPasswordValidator;

import nu.aaro.gustav.passwordstrengthmeter.PasswordStrengthCalculator;
import nu.aaro.gustav.passwordstrengthmeter.PasswordStrengthLevel;
import nu.aaro.gustav.passwordstrengthmeter.PasswordStrengthMeter;

public class LoginActivity extends AppCompatActivity {

    public final static String USERNAME_INTENT_NAME = "USERNAME";
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.gunnarro.android.simplepass.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        final EditText usernameEditText = binding.loginUsernameInput;
        final EditText encryptionKeyEditText = binding.loginEncryptionKeyInput;
        final Button loginButton = binding.loginBtn;
        TextInputLayout t;
        /*
        final PasswordStrengthMeter passwordStrengthMeter = binding.passwordInputMeter;
        PasswordStrengthLevel[] strengthLevels = {
                new PasswordStrengthLevel("Too short", android.R.color.darker_gray), // level 0
                new PasswordStrengthLevel("Weak", android.R.color.holo_red_dark), // level 1
                new PasswordStrengthLevel("Fair", android.R.color.holo_orange_dark), // level 2
                new PasswordStrengthLevel("Good", android.R.color.holo_orange_light), // level 3
                new PasswordStrengthLevel("Strong", android.R.color.holo_blue_light), // level 4
                new PasswordStrengthLevel("Very strong", android.R.color.holo_green_dark)}; // level 5
        passwordStrengthMeter.setStrengthLevels(strengthLevels);

        passwordStrengthMeter.setPasswordStrengthCalculator(new PasswordStrengthCalculator() {
            @Override
            public int calculatePasswordSecurityLevel(String password) {
                // Do some calculation and return an int corresponding to the "points" or "level" the user password got
                return CustomPasswordValidator.passwordStrengthNumber(password);
            }

            @Override
            public int getMinimumLength() {
                // Define the minimum length of a password. Anything below this should always yield a score of 0
                return 8;
            }

            @Override
            public boolean passwordAccepted(int level) {
                // Define whether or not the level is an accepted level or not.
                return level > 3;
            }

            @Override
            public void onPasswordAccepted(String password) {
                // Called when the password entered meets your requirements of length and strength levels
            }
        });
        */

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());

            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }

            if (loginFormState.getEncryptionKeyErrorMsg() != null) {
                encryptionKeyEditText.setError(loginFormState.getEncryptionKeyErrorMsg());
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }

            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }

            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
                setResult(Activity.RESULT_OK);
                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(), encryptionKeyEditText.getText().toString());
            }
        };

        encryptionKeyEditText.addTextChangedListener(afterTextChangedListener);
        encryptionKeyEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(usernameEditText.getText().toString(), encryptionKeyEditText.getText().toString());
            }
            return false;
        });
        loginButton.setOnClickListener(v -> loginViewModel.login(usernameEditText.getText().toString(), encryptionKeyEditText.getText().toString()));
    }

    private void updateUiWithUser(LoggedInUserView model) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(USERNAME_INTENT_NAME, model.getUserName());
        startActivity(intent);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Log.i("LoginActivity.showLoginFailed", "login failed, " + errorString);
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
    }
}