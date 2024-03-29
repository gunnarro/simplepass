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
import com.gunnarro.android.simplepass.domain.entity.Message;
import com.gunnarro.android.simplepass.domain.entity.User;
import com.gunnarro.android.simplepass.repository.CredentialDao;
import com.gunnarro.android.simplepass.repository.MessageDao;
import com.gunnarro.android.simplepass.repository.SettingsDao;
import com.gunnarro.android.simplepass.repository.UserDao;

import net.sqlcipher.database.SupportFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread safe database instance.
 * Backup is turned off for this database file.
 */
@Database(entities = {User.class, Credential.class, Settings.class, Message.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final String ENCRYPTED_SHARED_PREFS_NAME = "com.gunnarro.android.simplepass.encrypted_shared_prefs";
    private static final String PREFS_KEY_DB_PASSPHRASE = "PREFS_KEY_DB_PASSPHRASE";
    private static final String PREFS_KEY_MASTER_PASS = "PREFS_KEY_MASTER_PASS";
    private static final String DATABASE_NAME = "simple_cred_store_encrypted_db";
    // mutable thread-safe singleton
    private static AppDatabase dbInstance;

    /**
     * Thread safe access to the database.
     * Encrypted database
     */
    public static synchronized AppDatabase getDatabaseEncrypted(final Context context) throws GeneralSecurityException, IOException {
        String passphrase = getDbPassphrase(context);
        if (passphrase == null) {
            passphrase = initializeDbPassphrase(context);
        }

        if (dbInstance == null) {
            // Allow only single single thread access to the database
            dbInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
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
     * Shared Preferences is the way in which one can store and retrieve small amounts of primitive data as key/value pairs to a file on the device storage.
     * The data stored using shared preferences are kept private within the scope of the application
     * Generates a passphrase, simply a random string, and stores it in the encrypted shared preferences.
     * Returns the newly generated passphrase.
     */
    private static String initializeDbPassphrase(Context context) throws GeneralSecurityException, IOException {
        String passphrase = UUID.randomUUID().toString();
        saveDbPassPhrase(context, passphrase);
        return passphrase;
    }

    /**
     * Retrieves the passphrase for encryption from the encrypted shared preferences.
     * Returns null if there is no stored passphrase.
     */
    private static String getDbPassphrase(Context context) throws GeneralSecurityException, IOException {
        return getEncryptedSharedPrefs(context).getString(PREFS_KEY_DB_PASSPHRASE, null);
    }

    private static void saveDbPassPhrase(Context context, String passphrase) throws GeneralSecurityException, IOException {
        getEncryptedSharedPrefs(context).edit().putString(PREFS_KEY_DB_PASSPHRASE, passphrase).apply();
        Log.d("AppDatabase.savePassPhrase", passphrase);
    }

    private static void saveEncryptionMasterPass(Context context, String masterPass) throws GeneralSecurityException, IOException {
        getEncryptedSharedPrefs(context).edit().putString(PREFS_KEY_MASTER_PASS, masterPass).apply();
    }

    public static String getEncryptionMasterPass(Context context) throws GeneralSecurityException, IOException {
        return getEncryptedSharedPrefs(context).getString(PREFS_KEY_MASTER_PASS, null);
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
        getEncryptedSharedPrefs(context).edit().remove(PREFS_KEY_MASTER_PASS).apply();
    }

    /**
     * EncryptedSharedPreferences wraps the SharedPreferences class and automatically encrypts keys and values using a two-scheme method:
     * Returns a reference to the encrypted shared preferences. Ensure that backup is turned off.
     */
    private static SharedPreferences getEncryptedSharedPrefs(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                context,
                ENCRYPTED_SHARED_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public abstract CredentialDao credentialDao();

    public abstract UserDao userDao();

    public abstract SettingsDao settingsDao();

    public abstract MessageDao messageDao();
}