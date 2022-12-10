package com.gunnarro.android.simplepass.repository;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.entity.User;
import com.gunnarro.android.simplepass.exception.SimpleCredStoreApplicationException;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

public class UserRepository {

    private final UserDao userDao;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean isFirstTimeLogin() throws Exception {
        return getUsers().size() == 0;
    }

    public List<User> getUsers() throws Exception {
        Log.d("UserRepository.getUsers", "start ...");
        Callable<List<User>> callableGetUsersTask = userDao::getUsers;
        CompletionService<List<User>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(callableGetUsersTask);
        try {
            Future<List<User>> future = service.take();
            return future.get();
        } catch (Exception e) {
            throw new SimpleCredStoreApplicationException("get users failures!", "5000", e.getCause());
        }
    }

    /**
     * Room executes all queries on a separate thread.
     * Observed LiveData will notify the observer when the data has changed.
     */
    public User findUser(String username) {
        Log.d("UserRepository.findUser", "user: " + username);
        Callable<User> callableFindUserTask = () -> {
            Log.d("UserRepository.findUser", "current users: " + userDao.getUsers());
            return userDao.getByUsername(username);
        };
        Future<User> result = AppDatabase.databaseExecutor.submit(callableFindUserTask);
        while (!result.isDone()) {
            try {
                return result.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new SimpleCredStoreApplicationException("Error find user!", "5005", e);
            }
        }
        return null;
    }

    public void updateFailedLoginAttempts(String username) {
        AppDatabase.databaseExecutor.execute(() -> {
            User user = userDao.getByUsername(username);
            if (user != null) {
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                userDao.updateFailedLoginAttempts(user);
            } else {
                Log.d("UserRepository.updateFailedLoginAttempts", "user do not exist. user: " + username);
            }
        });
    }

    public User getByUsername(String username) {
        return userDao.getByUsername(username);
    }

    public void insert(User user) {
        AppDatabase.databaseExecutor.execute(() -> {
            try {
                userDao.insert(user);
                Log.d("UserRepository.insert", "created new user. user: " + user.getUsername());
            } catch (SQLiteConstraintException e) {
                throw new SimpleCredStoreApplicationException("Error inser user!", "5010", e);
            }
        });
    }

    public void delete(User user) {
        AppDatabase.databaseExecutor.execute(() -> userDao.delete(user));
    }
}
