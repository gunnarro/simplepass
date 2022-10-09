package com.gunnarro.android.simplepass.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.simplepass.domain.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    /**
     * Return all users
     * Do not return user where failed login attempt is greater than allowed
     * @return
     */
    @Query("SELECT * FROM user")
    List<User> getUsers();

    @Query("SELECT * FROM user WHERE username = :username")
    User getByUsername(String username);

    @Query("SELECT failed_login_attempts FROM user WHERE id = :id")
    Integer getFailedLoginAttempts(Long id);

    /**
     * Abort if user already exist
     * @param user user to be inserted
     */
    @Insert
    void insert(User user);

    @Update
    void updateFailedLoginAttempts(User user);

    /**
     * @param user to be deleted
     */
    @Delete
    void delete(User user);
}
