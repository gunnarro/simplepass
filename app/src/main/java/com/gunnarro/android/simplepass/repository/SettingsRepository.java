package com.gunnarro.android.simplepass.repository;

import android.app.Application;
import android.util.Log;

import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.config.Settings;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SettingsRepository {

    private final SettingsDao settingsDao;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    @Inject
    public SettingsRepository(Application application) throws GeneralSecurityException, IOException {
        Log.d("SettingsRepository", "init database..");
        settingsDao = AppDatabase.getDatabaseEncrypted(application).settingsDao();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public Settings getSettings() {
        return settingsDao.getAll().get(0);
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Settings settings) {
        AppDatabase.databaseExecutor.execute(() -> {
            settingsDao.insert(settings);
            Log.d("SettingsRepository.insert", settings.toString());
        });
    }

    public void update(Settings settings) {
        AppDatabase.databaseExecutor.execute(() -> {
            settingsDao.update(settings);
            Log.d("SettingsRepository.update", settings.toString());
        });
    }

    public void delete(Settings settings) {
        AppDatabase.databaseExecutor.execute(() -> {
            settingsDao.delete(settings);
            Log.d("SettingsRepository.delete", settings.toString());
        });
    }
}
