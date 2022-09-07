package com.gunnarro.android.simplepass.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.entity.Credential;

import java.util.List;

public class CredentialRepository {

    private final CredentialDao credentialDao;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public CredentialRepository(Application application) {
        credentialDao = AppDatabase.getDatabase(application).credentialDao();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Credential>> getAllCredentials() {
        Log.d("CredentialRepository.getAllCredentials", "refresh live data in fragment");
        return credentialDao.getAll();
    }

    public void delete(Credential credential) {
        AppDatabase.databaseExecutor.execute(() -> {
            credentialDao.delete(credential);
            Log.d("CredentialRepository.save", "deleted, id=" + credential.getId());
        });
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void save(Credential credential) {
        Log.d("CredentialRepository.save", "start save: " + credential);
        AppDatabase.databaseExecutor.execute(() -> {
            Credential credentialExisting = credentialDao.getByUsername(credential.getUsername());
            if (credentialExisting == null) {
                Long id = credentialDao.insert(credential);
                Log.d("CredentialRepository.save", "inserted (new), id=" + credentialDao.getById(id));
            } else {
                credential.setId(credentialExisting.getId()); // FIXME hack
                Log.d("CredentialRepository.save", "update: " + credential);
                credentialDao.update(credential);
                Log.d("CredentialRepository.save", "updated: " + credentialDao.getById(credential.getId()));
            }
        });
    }
}
