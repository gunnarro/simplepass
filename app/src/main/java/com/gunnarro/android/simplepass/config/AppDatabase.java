package com.gunnarro.android.simplepass.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.gunnarro.android.simplepass.domain.config.Settings;
import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.domain.entity.User;
import com.gunnarro.android.simplepass.repository.CredentialDao;
import com.gunnarro.android.simplepass.repository.SettingsDao;
import com.gunnarro.android.simplepass.repository.UserDao;
import com.gunnarro.android.simplepass.utility.AESCrypto;

import net.sqlcipher.database.SupportFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread safe database instance.
 */
@Database(entities = {User.class, Credential.class, Settings.class}, version = 22)
public abstract class AppDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final String SHARED_PREFS_NAME = "com.gunnarro.android.simplepass.encrypted_shared_prefs";
    private static final String PREFS_KEY_DB_PASSPHRASE = "PREFS_KEY_DB_PASSPHRASE";
    private static final String PREFS_KEY_MASTER_PASSWORD = "PREFS_KEY_MASTER_PASS";
    // mutable thread-safe singleton
    private static AppDatabase dbInstance;

    /**
     * Thread safe access to the database.
     * Singleton pattern
     */
    public synchronized static AppDatabase getDatabase(final Context context) {
        if (dbInstance == null) {
            // Allow only single single thread access to the database
            dbInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "simple_cred_store_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return dbInstance;
    }

    /**
     * Thread safe access to the database.
     * Encrypted database
     */
    public synchronized static AppDatabase getDatabaseEncrypted(final Context context) throws GeneralSecurityException, IOException {
        String passphrase = getDbPassphrase(context);
        if (passphrase == null) {
            passphrase = initializeDbPassphrase(context);
        }

        if (dbInstance == null) {
            // Allow only single single thread access to the database
            dbInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "simple_cred_store_encrypted_db")
                    .fallbackToDestructiveMigration()
                    // .allowMainThreadQueries() do not use this, only for testing purpose
                    // From here will Room take over and integrate with SQLCipher for Android.
                    .openHelperFactory(new SupportFactory(passphrase.getBytes(StandardCharsets.UTF_8)))
                    .build();
            Log.d("AppDatabase.getDatabaseEncrypted", "init encrypted database");
        }
        Log.d("AppDatabase.getDatabaseEncrypted", "encrypted database finished");
        return dbInstance;
    }

    /**
     * Retrieves the passphrase for encryption from the encrypted shared preferences.
     * Returns null if there is no stored passphrase.
     */
    private static String getDbPassphrase(Context context) throws GeneralSecurityException, IOException {
        return getEncryptedSharedPrefs(context).getString(PREFS_KEY_DB_PASSPHRASE, null);
    }

    /**
     * Shared Preferences is the way in which one can store and retrieve small amounts of primitive data as key/value pairs to a file on the device storage.
     * The data stored using shared preferences are kept private within the scope of the application
     * Generates a passphrase and stores it in the encrypted shared preferences.
     * Returns the newly generated passphrase.
     */
    private static String initializeDbPassphrase(Context context) throws GeneralSecurityException, IOException {
        String passphrase = AESCrypto.generatePassphrase();
        saveDbPassPhrase(context, passphrase);
        return passphrase;
    }

    private static void saveDbPassPhrase(Context context, String passphrase) throws GeneralSecurityException, IOException {
        getEncryptedSharedPrefs(context).edit().putString(PREFS_KEY_DB_PASSPHRASE, passphrase).apply();
        Log.d("AppDatabase.savePassPhrase", passphrase);
    }

    private static void saveEncryptionMasterPass(Context context, String masterPass) throws GeneralSecurityException, IOException {
        getEncryptedSharedPrefs(context).edit().putString(PREFS_KEY_MASTER_PASSWORD, masterPass).apply();
        Log.d("AppDatabase.saveEncryptionMasterPass", PREFS_KEY_MASTER_PASSWORD + "=" + masterPass);
    }

    public static String getEncryptionMasterPass(Context context) throws GeneralSecurityException, IOException {
        return getEncryptedSharedPrefs(context).getString(PREFS_KEY_MASTER_PASSWORD, null);
    }

    public static boolean isFingerprintLoginEnabled(Context context) throws GeneralSecurityException, IOException {
        return getEncryptionMasterPass(context) != null;
    }

    public static void enableFingerprintLogin(Context context, String masterPass) throws GeneralSecurityException, IOException {
        saveEncryptionMasterPass(context, masterPass);
        Log.d("AppDatabase.enableFingerprintLogin", "enabled fingerprint login");
    }

    public static void disableFingerprintLogin(Context context) throws GeneralSecurityException, IOException {
        deleteEncryptionMasterPass(context);
        Log.d("AppDatabase.disableFingerprintLogin", "disabled fingerprint login");
    }

    private static void deleteEncryptionMasterPass(Context context) throws GeneralSecurityException, IOException {
        getEncryptedSharedPrefs(context).edit().remove(PREFS_KEY_MASTER_PASSWORD).apply();
        Log.d("AppDatabase.deleteEncryptionMasterPass", PREFS_KEY_MASTER_PASSWORD);
    }

    /**
     * EncryptedSharedPreferences wraps the SharedPreferences class and automatically encrypts keys and values using a two-scheme method:
     * Returns a reference to the encrypted shared preferences.
     */
    private static SharedPreferences getEncryptedSharedPrefs(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                context,
                SHARED_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public abstract CredentialDao credentialDao();

    public abstract UserDao userDao();

    public abstract SettingsDao settingsDao();
}