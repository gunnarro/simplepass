package com.gunnarro.android.simplepass.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class AdminFragment extends Fragment implements View.OnClickListener {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Inject
    public AdminFragment() {
        // Needed by dagger framework
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_admin);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        Credential credential = new Credential();
        // check if this is an existing or a new credential
        String credentialJson = getArguments() != null ? getArguments().getString(CredentialListFragment.CREDENTIALS_JSON_INTENT_KEY) : null;
        if (credentialJson != null) {
            try {
                credential = mapper.readValue(credentialJson, Credential.class);
                Log.d(Utility.buildTag(getClass(), "onFragmentResult"), String.format("action: %s, credentials: %s", credentialJson, credential));
            } catch (JsonProcessingException e) {
                Log.e("", e.toString());
                throw new RuntimeException("Application Error: " + e);
            }
        }

        view.findViewById(R.id.change_master_password_btn).setOnClickListener(v -> {
            returnToCredentialList();
        });

        SwitchMaterial fingerprintLoginSwitch = view.findViewById(R.id.admin_disable_fingerprint_login_switch);
        try {
            boolean isEnabled = AppDatabase.isFingerprintLoginEnabled(getContext());
            fingerprintLoginSwitch.setChecked(isEnabled);
            fingerprintLoginSwitch.setEnabled(isEnabled);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fingerprintLoginSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                // first disable, because this is an irreversible operation
                buttonView.setEnabled(false);
                disableFingerprintLogin();
            }
        });

        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    private void returnToCredentialList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, CredentialListFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    /**
     * Update backup info after view is successfully create
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View view) {
        // ask every time
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You have not been granted access, ask for permission now.
            //requestPermissionLauncher.launch(Manifest.permission.ACCESS_MEDIA_LOCATION);
        } else {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, permission granted");
        }

        int id = view.getId();
        if (id == R.id.btn_credential_register_save) {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, save entry: ");
        } else if (id == R.id.btn_credential_register_cancel) {
            // return back to main view
        }
    }

    private void disableFingerprintLogin() {
        try {
            AppDatabase.disableFingerprintLogin(getContext());
        } catch (GeneralSecurityException | IOException e) {
           Log.e("", e.getMessage());
        }
    }
}
