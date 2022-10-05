package com.gunnarro.android.simplepass.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.databinding.ActivityLoginBinding;
import com.gunnarro.android.simplepass.ui.MainActivity;

public class LoginActivity extends AppCompatActivity {

    public final static String LOGGED_IN_USER_ID_INTENT_KEY = "LOGGED_IN_USER_ID";
    public final static String USERNAME_INTENT_KEY = "USERNAME";
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        final EditText usernameEditText = loginBinding.loginUsernameInput;
        final EditText encryptionKeyEditText = loginBinding.loginEncryptionKeyInput;
        final Button loginButton = loginBinding.loginBtn;
        try {
            if (loginViewModel.isFistTimeLogin()) {
                // do not show fingerprint login button if first time login
                loginBinding.loginFingerprintBtn.setVisibility(View.INVISIBLE);
                // only show username input for first time login (registration), thereafter only password is needed.
                findViewById(R.id.login_username_input_layout).setVisibility(View.VISIBLE);
                usernameEditText.requestFocus();

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
            } else {
                loginViewModel.getLoginFormState().observe(this, loginFormState -> {
                    if (loginFormState == null) {
                        return;
                    }
                    loginButton.setEnabled(true);
                });
            }
            // common
            loginViewModel.getLoginResult().observe(this, loginResult -> {
                if (loginResult == null) {
                    return;
                }

                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }

                if (loginResult.getLoggedInUseDto() != null) {
                    showMainActivity(loginResult.getLoggedInUseDto().getId());
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
            // username/password login
            loginButton.setOnClickListener(v -> loginViewModel.login(usernameEditText.getText().toString(), encryptionKeyEditText.getText().toString()));
            // fingerprint login dialog
            loginBinding.loginFingerprintBtn.setOnClickListener(view -> createBiometricPrompt().authenticate(createBiometricPromptInfo()));
        } catch (Exception e) {
            Log.e("", e.getMessage());
            showLoginFailed(R.string.login_failed);
        }
    }

    private void showMainActivity(Long loggedInUserId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LOGGED_IN_USER_ID_INTENT_KEY, loggedInUserId);
        startActivity(intent);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Log.i("LoginActivity.showLoginFailed", "login failed, " + errorString);
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
    }

    private void startLoginFingerprintActivity() {
        Intent intent = new Intent(this, LoginFingerprintActivity.class);
        startActivity(intent);
    }

    private BiometricPrompt createBiometricPrompt() {
        return new BiometricPrompt(LoginActivity.this, ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errorMsg) {
                        super.onAuthenticationError(errorCode, errorMsg);
                        if ("Back to user/password login".contentEquals(errorMsg)) {
                            // nothing to do, simply close fingerprint login dialog and stay in this view
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication error: " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        String principal = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                           // principal = result.getCryptoObject().getIdentityCredential().getCredentialKeyCertificateChain().stream().distinct().findAny().orElse(null).getIssuerDN().toString();
                        }
                        loginViewModel.login("username", "master-password");
                        Log.d("onAuthenticationSucceeded", "" + principal);
                        showMainActivity(1L);
                        setResult(Activity.RESULT_OK);
                        //Complete and destroy login activity once successful
                        finish();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private BiometricPrompt.PromptInfo createBiometricPromptInfo() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login with fingerprint")
                .setSubtitle("Log in using your fingerprint")
                .setNegativeButtonText("Back to user/password login")
                .build();
    }
}