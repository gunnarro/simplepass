package com.gunnarro.android.simplepass.ui.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.repository.CredentialRepository;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 * We use AndroidViewModel because we can pass the application context in the constructor.
 */
public class CredentialViewModel extends AndroidViewModel {

    private final CredentialRepository repository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    private final LiveData<List<Credential>> credentials;

    public CredentialViewModel(@NonNull Application application) {
        super(application);
        repository = new CredentialRepository(application);
        credentials = repository.getAllCredentials();
    }

    public LiveData<List<Credential>> getCredentialLiveData() {
        return credentials;
    }

    public void save(Credential credential) {
        Log.d("CredentialViewModel.save" , "save: " + credential);
        repository.save(credential);
    }

    public void delete(Credential credential) {
        Log.d("CredentialViewModel.delete" , "save: " + credential);
        repository.delete(credential);
    }
}
