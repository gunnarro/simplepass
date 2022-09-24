package com.gunnarro.android.simplepass.ui.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.repository.CredentialRepository;
import com.gunnarro.android.simplepass.validator.CustomPasswordValidator;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 * We use AndroidViewModel because we can pass the application context in the constructor.
 */
public class CredentialViewModel extends AndroidViewModel {

    private final CredentialRepository credentialRepository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    private final LiveData<List<Credential>> credentials;

    public CredentialViewModel(@NonNull Application application) {
        super(application);
        credentialRepository = new CredentialRepository(application);
        credentials = credentialRepository.getAllCredentials();
    }

    public LiveData<List<Credential>> getCredentialLiveData() {
        return credentials;
    }

    public void save(Credential credential) {
        Log.d("CredentialViewModel.save" , "save: " + credential);
        credentialRepository.save(credential);
    }

    public void delete(Credential credential) {
        Log.d("CredentialViewModel.delete" , "save: " + credential);
        credentialRepository.delete(credential);
    }

    public void credentialDataChanged(String systemName, String userName, String password) {
        if (!isUsernameValid(userName)) {
          //  credentialFormState.setValue(new CredentialFormState(R.string.invalid_username, null, null, null));
        } else if (!isSystemNameValid(systemName)) {
          //  credentialFormState.setValue(new CredentialFormState(null, 23, null, null));
        }
    }

    // A placeholder encryptionKey validation check
    private List<String> isEncryptionKeyValid(String encryptionKey) {
        return CustomPasswordValidator.passwordStrength(encryptionKey);
    }

    private boolean isSystemNameValid(String systemName) {
        return systemName != null && systemName.trim().length() > 1;
    }

    private boolean isUsernameValid(String username) {
        return username != null && username.trim().length() > 1;
    }
}
