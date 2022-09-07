package com.gunnarro.android.simplepass.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.gunnarro.android.simplepass.domain.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<User> getUsers();

    @Query("SELECT * FROM user WHERE username = :username")
    User getByUsername(String username);

    /**
     * Abort if user already exist
     * @param user user to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(User user);

    /**
     * @param user to be deleted
     */
    @Delete
    void delete(User user);
}
