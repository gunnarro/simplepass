package com.gunnarro.android.simplepass.config;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.gunnarro.android.simplepass.domain.config.Settings;
import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.domain.entity.User;
import com.gunnarro.android.simplepass.repository.CredentialDao;
import com.gunnarro.android.simplepass.repository.SettingsDao;
import com.gunnarro.android.simplepass.repository.UserDao;

import net.sqlcipher.database.SupportFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread safe database instance.
 */
@Database(entities = {User.class, Credential.class, Settings.class}, version = 18)
public abstract class AppDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    // Called when the database is created for the first time. This is called after all the tables are created.
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d("RoomDatabase.Callback", "start init database");
            // this method is called when database is created
            // and below line is to populate our data.
            // new PopulateDbAsyncTask(INSTANCE).execute();
        }
    };
    private static AppDatabase INSTANCE;

    /**
     * Thread safe access to the database.
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // Allow only single single thread access to the database
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "simplepass_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Encrypted database
     */
    public static AppDatabase getDatabaseEncrypted(final Context context, final String masterPassword) {
        if (INSTANCE == null) {
            // Allow only single single thread access to the database
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "simplepass_database")
                            .fallbackToDestructiveMigration()
                            // From here will Room will take over and integrate with SQLCipher for Android.
                            .openHelperFactory(new SupportFactory(masterPassword.getBytes(StandardCharsets.UTF_8)))
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract CredentialDao credentialDao();

    public abstract UserDao userDao();

    public abstract SettingsDao settingsDao();
}