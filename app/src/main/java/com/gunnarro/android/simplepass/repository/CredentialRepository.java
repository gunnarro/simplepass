package com.gunnarro.android.simplepass.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.entity.Credential;

import java.util.List;

public class CredentialRepository {

    private final CredentialDao credentialDao;
    private final LiveData<List<Credential>> credentialList;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public CredentialRepository(Application application) {
        credentialDao = AppDatabase.getDatabase(application).credentialDao();
        credentialList = credentialDao.getAll();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Credential>> getAllCredentials() {
        Log.d("TimesheetRepository.getAllTimesheet", "refresh live data in fragment");
        return credentialDao.getAll();
    }

    public void insert(Credential credential) {
        AppDatabase.databaseWriteExecutor.execute(() -> credentialDao.insert(credential));
    }

    public void delete(Credential credential) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            credentialDao.delete(credential);
            Log.d("TimesheetRepository.save", "deleted, id=" + credential.getId());
        });
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void save(Credential credential) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Credential credentialExisting = credentialDao.getByUsername(credential.getUsername());
            if (credentialExisting == null) {
                Long id = credentialDao.insert(credential);
                Log.d("TimesheetRepository.save", "inserted (new), id=" + credentialDao.getById(id));
            } else {
                credential.setId(credentialExisting.getId()); // FIXME hack
                Log.d("TimesheetRepository.save", "update: " + credential);
                int rows = credentialDao.update(credential);
                Log.d("TimesheetRepository.save", "updated: " + credentialDao.getById(credential.getId()));
            }
        });
    }
}
