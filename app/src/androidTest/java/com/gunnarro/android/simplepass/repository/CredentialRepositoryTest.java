package com.gunnarro.android.simplepass.repository;

import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.utility.AESCrypto;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CredentialRepositoryTest {

    AppDatabase db;
    private CredentialRepository credentialRepository;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        credentialRepository = new CredentialRepository(db.credentialDao());
    }

    @After
    public void closeDb() {
        AESCrypto.reset();
        db.close();
    }

    @Test
    public void getCredentials_empty() {
        assertNull(credentialRepository.getAllCredentials(23L).getValue());
    }

}
