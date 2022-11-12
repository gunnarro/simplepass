package com.gunnarro.android.simplepass.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.EncryptedString;
import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.domain.entity.User;
import com.gunnarro.android.simplepass.utility.AESCrypto;
import com.gunnarro.android.simplepass.exception.CryptoException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

public class SimplePassDatabaseTest {

    private final static String USERNAME = "unit-test-user";
    AppDatabase db;
    private CredentialRepository credentialRepository;
    private UserRepository userRepository;

    @Before
    public void createDb() throws CryptoException {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        credentialRepository = new CredentialRepository(db.credentialDao());
        userRepository = new UserRepository(db.userDao());
        AESCrypto.init("master-pass");
        insertTestData();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void saveCredential() {
        User user = userRepository.getByUsername(USERNAME);
        Credential credential = new Credential();
        credential.setFkUserId(user.getId());
        credential.setCreatedDate(LocalDateTime.now());
        credential.setLastModifiedDate(LocalDateTime.now());
        credential.setSystem("github");
        credential.setUsername("gunnarro");
        credential.setPassword(new EncryptedString("my-encrypted-password"));
        Long id = credentialRepository.save(credential);
        Credential savedCredential = credentialRepository.getCredential(id);
        assertEquals(credential.getFkUserId(), savedCredential.getFkUserId());
        assertNotNull(savedCredential.getCreatedDate());
        assertNotNull(savedCredential.getLastModifiedDate());
        assertEquals(credential.getSystem(), savedCredential.getSystem());
        assertEquals(credential.getUsername(), savedCredential.getUsername());
        assertEquals(credential.getPassword().getValue(), savedCredential.getPassword().getValue());
    }

    @Test
    public void getCredentials() {
        User user = userRepository.getByUsername(USERNAME);
        assertEquals(1, credentialRepository.getCredentials(user.getId()).size());
        assertEquals("master-password", credentialRepository.getCredentials(user.getId()).get(0).getPassword().getValue());
    }

    @Test
    public void credential_update_password() {
        User user = userRepository.getByUsername(USERNAME);
        List<Credential> credentials = credentialRepository.getCredentials(user.getId());
        assertEquals("master-password", credentials.get(0).getPassword().getValue());
        Credential updatedCredential = credentials.get(0);
        updatedCredential.setPassword(new EncryptedString("updated-master-password"));
        credentialRepository.save(updatedCredential);
        assertEquals("updated-master-password", credentialRepository.getCredentials(user.getId()).get(0).getPassword().getValue());
    }

    @Test
    public void getCredentials_unknown_user() {
        assertEquals(0, credentialRepository.getCredentials(23L).size());
    }

    /**
     * Should never happen that a user are logged in with wrong password.
     * Test only that values not are decrypted if wrong master password
     */
    @Test
    public void getCredentials_wrong_master_pass() throws CryptoException {
        AESCrypto.reset();
        AESCrypto.init("wrong-master-pass");
        User user = userRepository.getByUsername(USERNAME);
        assertEquals(1, credentialRepository.getCredentials(user.getId()).size());
        assertNotEquals("master-password", credentialRepository.getCredentials(user.getId()).get(0).getPassword().getValue());
    }

    private void insertTestData() {
        userRepository.insert(new User(USERNAME));
        User user = userRepository.getByUsername(USERNAME);
        Credential credential = new Credential();
        credential.setFkUserId(user.getId());
        credential.setCreatedDate(LocalDateTime.now());
        credential.setLastModifiedDate(LocalDateTime.now());
        credential.setSystem("unit-test");
        credential.setUsername("test-user");
        credential.setPassword(new EncryptedString("master-password"));
        credentialRepository.save(credential);
    }
}
