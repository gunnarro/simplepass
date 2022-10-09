package com.gunnarro.android.simplepass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.EncryptedString;
import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.domain.entity.User;
import com.gunnarro.android.simplepass.repository.CredentialRepository;
import com.gunnarro.android.simplepass.repository.UserRepository;
import com.gunnarro.android.simplepass.utility.AESCrypto;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntegrationTest {

    AppDatabase db;

    private CredentialRepository credentialRepository;
    private UserRepository userRepository;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        credentialRepository = new CredentialRepository(db.credentialDao());
        userRepository = new UserRepository(db.userDao());
        userRepository.insert(new User("unit-test-user"));
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void saveCredential() throws Exception {
        User user = userRepository.getUsers().get(0);
        assertNotNull(user);
        Credential newCredential = new Credential();
        newCredential.setFkUserId(user.getId());
        newCredential.setSystem("github");
        newCredential.setUsername("gunnarro");
        newCredential.setPassword(new EncryptedString("my-pass"));
        Long id = credentialRepository.save(newCredential);
        // must wait for executor thread to finish
        Thread.sleep(1000L);
        Credential savedCredential = credentialRepository.getCredential(id);
        assertEquals(savedCredential, newCredential);
    }

    @Test
    public void saveCredential_duplicate() throws Exception {
        User user = userRepository.getUsers().get(0);
        assertNotNull(user);
        AESCrypto.init("encryptionKey");
        Credential newCredential = new Credential();
        newCredential.setFkUserId(user.getId());
        newCredential.setSystem("github");
        newCredential.setUsername("gunnarro");
        newCredential.setPassword(new EncryptedString("my-pass"));
        credentialRepository.save(newCredential);
        try {
            credentialRepository.save(newCredential);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("code 2067 SQLITE_CONSTRAINT_UNIQUE"));
        }
    }
}
