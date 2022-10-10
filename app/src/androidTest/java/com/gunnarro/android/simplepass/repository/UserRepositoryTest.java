package com.gunnarro.android.simplepass.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.entity.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserRepositoryTest {

    AppDatabase db;

    private UserRepository userRepository;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        userRepository = new UserRepository(db.userDao());
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void getUsers() throws Exception {
        assertEquals(0, userRepository.getUsers().size());
    }

    @Test
    public void finUSer_not_found() {
        assertNull(userRepository.findUser("invalid-user"));
    }

    @Test
    public void deleteUser_not_found() {
        userRepository.delete(new User("user"));
        assertTrue(true);
    }

    @Test
    public void insertUser() {
        userRepository.insert(new User("new-user"));
        assertEquals("new-user", userRepository.findUser("new-user").getUsername());
    }

    @Test
    public void insertUser_duplicate() throws Exception {
        userRepository.insert(new User("new-user"));
        userRepository.insert(new User("new-user"));
        assertEquals(1, userRepository.getUsers().size());
    }

}
