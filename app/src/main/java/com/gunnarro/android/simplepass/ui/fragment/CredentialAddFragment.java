package com.gunnarro.android.simplepass.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.domain.EncryptedString;
import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.utility.Utility;
import com.gunnarro.android.simplepass.validator.CustomPasswordValidator;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class CredentialAddFragment extends Fragment implements View.OnClickListener {

    // Match a not-empty string. A string with only spaces or no characters is an empty-string.
    public static final String HAS_TEXT_REGEX = "\\w.*+";
    // match one or more withe space
    public static final String EMPTY_TEXT_REGEX = "\\s+";
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Inject
    public CredentialAddFragment() {
        // Needed by dagger framework
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_credential_add);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_credential_add, container, false);
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

        updateAddCredentialView(view, credential);
        // disable save button as default
        view.findViewById(R.id.btn_credential_register_save).setEnabled(false);
        view.findViewById(R.id.btn_credential_register_save).setOnClickListener(v -> {
            view.findViewById(R.id.btn_credential_register_save).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(CredentialListFragment.CREDENTIALS_JSON_INTENT_KEY, getCredentialsAsJson());
            result.putString(CredentialListFragment.CREDENTIALS_ACTION_KEY, CredentialListFragment.CREDENTIALS_ACTION_SAVE);
            getParentFragmentManager().setFragmentResult(CredentialListFragment.CREDENTIALS_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new item intent");
            returnToCredentialList();
        });

        view.findViewById(R.id.btn_credential_register_delete).setOnClickListener(v -> {
            view.findViewById(R.id.btn_credential_register_delete).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(CredentialListFragment.CREDENTIALS_JSON_INTENT_KEY, getCredentialsAsJson());
            result.putString(CredentialListFragment.CREDENTIALS_ACTION_KEY, CredentialListFragment.CREDENTIALS_ACTION_DELETE);
            getParentFragmentManager().setFragmentResult(CredentialListFragment.CREDENTIALS_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new item intent");
            returnToCredentialList();
        });

        view.findViewById(R.id.btn_credential_register_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_credential_register_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to credential list
            NavigationView navigationView = requireActivity().findViewById(R.id.navigationView);
            requireActivity().onOptionsItemSelected(navigationView.getMenu().findItem(R.id.nav_credential_list));
            returnToCredentialList();
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

    private void updateAddCredentialView(View view, Credential credential) {
        TextView id = view.findViewById(R.id.credential_entity_id);
        id.setText(String.valueOf(credential.getId()));

        TextView userId = view.findViewById(R.id.credential_user_id);
        userId.setText(String.valueOf(credential.getFkUserId()));

        EditText createdDateView = view.findViewById(R.id.credential_created_date);
        createdDateView.setText(Utility.formatDateTime(credential.getCreatedDate()));

        EditText lastModifiedDateView = view.findViewById(R.id.credential_last_modified_date);
        lastModifiedDateView.setText(Utility.formatDateTime(credential.getLastModifiedDate()));

        EditText systemView = view.findViewById(R.id.credential_system);
        systemView.setText(credential.getSystem());
        systemView.addTextChangedListener(createEmptyTextValidator(systemView, HAS_TEXT_REGEX, "Can not be empty!"));
        systemView.requestFocus();

        EditText usernameView = view.findViewById(R.id.credential_username);
        usernameView.addTextChangedListener(createEmptyTextValidator(usernameView, HAS_TEXT_REGEX, "Can not be empty!"));
        usernameView.setText(credential.getUsername());

        EditText passwordView = view.findViewById(R.id.credential_password);
        passwordView.addTextChangedListener(createEmptyTextValidator(passwordView, HAS_TEXT_REGEX, "Can not be empty!"));
        passwordView.setText(credential.getPassword() != null ? credential.getPassword().getValue() : null);
        passwordView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // means that user is finished to type text, the check if save button should be enabled
                view.findViewById(R.id.btn_credential_register_save).setEnabled(isInputFormDataValid());
            }
        });

        // hide fields if this is a new
        if (credential.getId() == null) {
            view.findViewById(R.id.credential_created_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.credential_last_modified_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.credential_password_broken_validation_rules_layout).setVisibility(View.GONE);
            view.findViewById(R.id.btn_credential_register_delete).setVisibility(View.GONE);
        } else {
            TextInputLayout layout = view.findViewById(R.id.credential_password_layout);
            layout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            TextInputEditText passwordValidation = view.findViewById(R.id.credential_password_broken_validation_rules);
            String brokenRules = new CustomPasswordValidator().passwordStrengthValidation(credential.getPassword().getValue()).stream().map(String::toString).collect(Collectors.joining("\n"));
            if (!brokenRules.isEmpty()) {
                passwordValidation.setText(brokenRules.replace(":", ":\n"));
            } else {
                // nothing to show hide field
                view.findViewById(R.id.credential_password_broken_validation_rules_layout).setVisibility(View.GONE);
            }
        }
        Log.d(Utility.buildTag(getClass(), "updateAddCredentialView"), String.format("updated %s ", credential));
    }

    @Override
    public void onClick(View view) {
        // ask every time
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You have not been granted access, ask for permission now, no need for nay permissions
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, permission not granted");
        }

        int id = view.getId();
        if (id == R.id.btn_credential_register_save) {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, save entry");
        } else if (id == R.id.btn_credential_register_cancel) {
            // return back to main view
            Log.d(Utility.buildTag(getClass(), "onClick"), "cancel button, return back to credential list view");
        }
    }

    private String getCredentialsAsJson() {
        Credential credential = new Credential();
        TextView idView = requireView().findViewById(R.id.credential_entity_id);
        Log.d("getCredentialsAsJson", "idView=" + idView.getText() + ", is=" + (idView.getText().length()));
        credential.setId(Utility.isInteger(idView.getText().toString()) ? Long.parseLong(idView.getText().toString()) : null);

        TextView userIdView = requireView().findViewById(R.id.credential_user_id);
        credential.setFkUserId(Utility.isInteger(userIdView.getText().toString()) ? Long.parseLong(userIdView.getText().toString()) : null);

        EditText createdDateView = requireView().findViewById(R.id.credential_created_date);
        LocalDateTime createdDateTime = Utility.toLocalDateTime(createdDateView.getText().toString());
        if (createdDateTime != null) {
            credential.setCreatedDate(createdDateTime);
        }

        EditText lastModifiedDateView = requireView().findViewById(R.id.credential_last_modified_date);
        LocalDateTime lastModifiedDateTime = Utility.toLocalDateTime(lastModifiedDateView.getText().toString());
        if (lastModifiedDateTime != null) {
            credential.setLastModifiedDate(lastModifiedDateTime);
        }

        EditText systemView = requireView().findViewById(R.id.credential_system);
        credential.setSystem(systemView.getText().toString());

        EditText usernameView = requireView().findViewById(R.id.credential_username);
        credential.setUsername(usernameView.getText().toString());

        EditText passwordView = requireView().findViewById(R.id.credential_password);
        credential.setPassword(new EncryptedString(passwordView.getText().toString()));
        credential.setPasswordStatus(new CustomPasswordValidator().passwordStrengthStatus(passwordView.getText().toString()));

        try {
            return Utility.getJsonMapper().writerWithDefaultPrettyPrinter().writeValueAsString(credential);
        } catch (JsonProcessingException e) {
            Log.e("getCredentialsAsJson", e.toString());
            throw new RuntimeException("unable to parse object to json! " + e);
        }
    }

    /**
     *
     */
    private TextWatcher createEmptyTextValidator(EditText editText, String regexp, String validationErrorMsg) {
        return new TextWatcher() {
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
                if (!editText.getText().toString().matches(regexp)) {
                    editText.setError(validationErrorMsg);
                }
            }
        };
    }

    private boolean isInputFormDataValid() {
        return true;
    }
}
