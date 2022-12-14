package com.gunnarro.android.simplepass.ui.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.gunnarro.android.simplepass.R;

public class LoginFingerprintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            checkBiometric();
        }
        setContentView(R.layout.activity_fingerprint_login);

        BiometricPrompt biometricPrompt = new BiometricPrompt(
                LoginFingerprintActivity.this,
                ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errorMsg) {
                        super.onAuthenticationError(errorCode, errorMsg);
                        if ("Back to user/password login".contentEquals(errorMsg)) {
                            startLoginActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication error: " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                    }

                });

        BiometricPrompt.PromptInfo promptInfo;
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login with fingerprint")
                .setSubtitle("Log in using your fingerprint")
                .setNegativeButtonText("Back to user/password login")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        Button biometricLoginButton = findViewById(R.id.biometric_login_btn);
        biometricLoginButton.setOnClickListener(view -> biometricPrompt.authenticate(promptInfo));
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void checkBiometric() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("LoginFingerprintActivity", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("LoginFingerprintActivity", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("LoginFingerprintActivity", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, "BIOMETRIC_STRONG | DEVICE_CREDENTIAL");
                Toast.makeText(this, "Biometric not found on device!", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED, BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                break;
            default:
                Toast.makeText(this, "Biometric not found on device!", Toast.LENGTH_LONG).show();
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}