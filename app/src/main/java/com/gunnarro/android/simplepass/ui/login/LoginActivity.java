package com.gunnarro.android.simplepass.ui.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.databinding.ActivityLoginBinding;
import com.gunnarro.android.simplepass.ui.MainActivity;

public class LoginActivity extends AppCompatActivity {

    public final static String LOGGED_IN_USER_ID_INTENT_KEY = "LOGGED_IN_USER_ID";

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
            if (loginViewModel.isFirstTimeLogin()) {
                // do not show fingerprint login button if first time login
                loginBinding.loginFingerprintBtn.setVisibility(View.INVISIBLE);
                // only show username input for first time login (registration), thereafter only password is needed.
                findViewById(R.id.login_username_input_layout).setVisibility(View.VISIBLE);
                usernameEditText.requestFocus();

                loginViewModel.getLoginFormState().observe(this, loginFormState -> {
                    Log.d("LoginActivity.getLoginFormState", "first time login, " + loginFormState.toString());
                    loginButton.setEnabled(loginFormState.isDataValid());

                    if (loginFormState.getUsernameError() != null) {
                        usernameEditText.setError(getString(loginFormState.getUsernameError()));
                    }

                    if (loginFormState.getEncryptionKeyErrorMsg() != null) {
                        encryptionKeyEditText.setError(loginFormState.getEncryptionKeyErrorMsg());
                    }
                });
            } else if (AppDatabase.isFingerprintLoginEnabled(getApplicationContext())) {
                Log.d("LoginActivity.onCreate", "use fingerprint login only");
                loginBinding.loginFingerprintBtn.setVisibility(View.VISIBLE);
                // hide everything else
                loginBinding.loginUsernameInputLayout.setVisibility(View.INVISIBLE);
                loginBinding.loginEncryptionKeyInputLayout.setVisibility(View.INVISIBLE);
                loginBinding.loginEnableFingerprintLoginSwitch.setVisibility(View.INVISIBLE);
                loginBinding.loginBtn.setVisibility(View.INVISIBLE);
                // show fingerprint login dialog
                createBiometricPrompt().authenticate(createBiometricPromptInfo());
            } else {
                Log.d("LoginActivity.onCreate", "ordinary login, with user and pass");
                loginBinding.loginFingerprintBtn.setVisibility(View.INVISIBLE);
                loginViewModel.getLoginFormState().observe(this, loginFormState -> {
                    Log.d("LoginActivity.getLoginFormState", "login, " + loginFormState.toString());
                    loginButton.setEnabled(true);
                });
            }
            // common
            loginViewModel.getLoginResult().observe(this, loginResult -> {
                if (loginResult == null) {
                    return;
                }

                if (loginResult.getError() != null) {
                    showInfoDialog(getResources().getString(loginResult.getError()));
                }

                if (loginResult.getLoggedInUseDto() != null) {
                    showMainActivity(loginResult.getLoggedInUseDto().getId());
                    setResult(Activity.RESULT_OK);
                    //Complete and destroy login activity once login is successful
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
            // encryptionKeyEditText.addTextChangedListener(afterTextChangedListener);
            encryptionKeyEditText.setOnFocusChangeListener((v, hasFocus) -> {
                Log.d("LoginActivity.setOnFocusChangeListener", "check ..." + hasFocus);
                if (!hasFocus) {
                    loginViewModel.loginDataChanged(usernameEditText.getText().toString(), encryptionKeyEditText.getText().toString());
                }
            });
            /*
            encryptionKeyEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(), encryptionKeyEditText.getText().toString(), loginBinding.loginEnableFingerprintLogin.isChecked());
                }
                return false;
            });
             */
            // username/password login
            loginButton.setOnClickListener(v -> loginViewModel.login(usernameEditText.getText().toString(), encryptionKeyEditText.getText().toString(), loginBinding.loginEnableFingerprintLoginSwitch.isChecked()));
            // fingerprint login dialog
            // FIXME should use following
            // biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher)); // Compliant
            /**
             biometricLoginButton.setOnClickListener(view -> {
             // Exceptions are unhandled within this snippet.
             Cipher cipher = getCipher();
             SecretKey secretKey = getSecretKey();
             cipher.init(Cipher.ENCRYPT_MODE, secretKey);
             biometricPrompt.authenticate(promptInfo,
             new BiometricPrompt.CryptoObject(cipher));
             });

             */
            /*
            generateSecretKey(new KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationParameters(
                            VALIDITY_DURATION_SECONDS,
                            ALLOWED_AUTHENTICATORS
                    )
                    .build());
            */

            loginBinding.loginFingerprintBtn.setOnClickListener(view ->
                    createBiometricPrompt().authenticate(createBiometricPromptInfo())
            );
        } catch (Exception e) {
            Log.e("LoginActivity", e.getMessage());
            showInfoDialog(getResources().getString(R.string.login_failed));
        }
    }

    private void showMainActivity(Long loggedInUserId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LOGGED_IN_USER_ID_INTENT_KEY, loggedInUserId);
        startActivity(intent);
        // finally finish login activity
        finish();
    }

    private BiometricPrompt createBiometricPrompt() {
        return new BiometricPrompt(LoginActivity.this, ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errorMsg) {
                        super.onAuthenticationError(errorCode, errorMsg);
                        switch (errorCode) {
                            case BiometricPrompt.ERROR_USER_CANCELED,
                                    BiometricPrompt.ERROR_TIMEOUT,
                                    BiometricPrompt.ERROR_CANCELED,
                                    BiometricPrompt.ERROR_HW_NOT_PRESENT,
                                    BiometricPrompt.ERROR_HW_UNAVAILABLE,
                                    BiometricPrompt.ERROR_LOCKOUT,
                                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT,
                                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                                    BiometricPrompt.ERROR_NO_BIOMETRICS,
                                    BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL,
                                    BiometricPrompt.ERROR_NO_SPACE,
                                    BiometricPrompt.ERROR_UNABLE_TO_PROCESS,
                                    BiometricPrompt.ERROR_VENDOR:
                                return;
                            default:
                                showInfoDialog("Authentication error!\n" + errorMsg);
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        String principal = null;
                        // check if android version support fingerprint
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                            // do not support
                            if (result.getCryptoObject() != null) {
                                principal = result.getCryptoObject().getIdentityCredential().getCredentialKeyCertificateChain().stream().findFirst().toString();
                            } else {
                                principal = "generated-master-password";
                            }
                        }
                        try {
                            loginViewModel.loginFingerprint();
                        } catch (Exception e) {
                            showInfoDialog("Authentication error!\nError: " + e.getMessage());
                        }
                        Log.d("LoginActivity.onAuthenticationSucceeded", "" + principal + "version: " + android.os.Build.VERSION.SDK_INT + " -> " + android.os.Build.VERSION_CODES.R);
                        showMainActivity(1L);
                        setResult(Activity.RESULT_OK);
                        //Complete and destroy login activity once successful
                        finish();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        //showInfoDialog("Authentication failed, Please try again.");
                    }
                });
    }

    private BiometricPrompt.PromptInfo createBiometricPromptInfo() {
        Log.i("LoginActivity.createBiometricPromptInfo", "createBiometricPromptInfo ... ");
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login with fingerprint")
                .setSubtitle("Log in using your fingerprint")
                .setDescription("description goe here")
                .setNegativeButtonText("Back to user/password login")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build();
    }

    private void showInfoDialog(String infoMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Login failed!");
        builder.setMessage(infoMessage);
        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);
        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
    private void encryptSecretInformation() {
        // Exceptions are unhandled for getCipher() and getSecretKey().
        Cipher cipher = getCipher();
        SecretKey secretKey = getSecretKey();
        try {
            // NullPointerException is unhandled; use Objects.requireNonNull().
            ciper.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedInfo = cipher.doFinal("plaintext-string".getBytes(Charset.defaultCharset()));
        } catch (InvalidKeyException e) {
            Log.e("MY_APP_TAG", "Key is invalid.");
        } catch (UserNotAuthenticatedException e) {
            Log.d("MY_APP_TAG", "The key's validity timed out.");
            biometricPrompt.authenticate(promptInfo);
        }
    }*/

}