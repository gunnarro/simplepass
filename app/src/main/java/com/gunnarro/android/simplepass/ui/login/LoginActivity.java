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

import com.gunnarro.android.simplepass.databinding.ActivityLoginBinding;
import com.gunnarro.android.simplepass.ui.MainActivity;

public class LoginActivity extends AppCompatActivity {

    public final static String USERNAME_INTENT_NAME = "USERNAME";
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.gunnarro.android.simplepass.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        final EditText usernameEditText = binding.loginUsernameTxt;
        final EditText encryptionKeyEditText = binding.loginEncryptionKeyTxt;
        final Button loginButton = binding.loginBtn;

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());

            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }

            if (loginFormState.getEncryptionKeyError() != null) {
                encryptionKeyEditText.setError(getString(loginFormState.getEncryptionKeyError()));
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