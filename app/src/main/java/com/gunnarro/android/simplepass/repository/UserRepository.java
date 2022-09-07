package com.gunnarro.android.simplepass.repository;

import android.app.Application;
import android.util.Log;

import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.entity.User;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

public class UserRepository {

    private final UserDao userDao;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public UserRepository(Application application) {
        userDao = AppDatabase.getDatabase(application).userDao();
    }

    public List<User> getUsers() throws InterruptedException, ExecutionException {
        Log.d("UserRepository.getUsers", "start ...");
        List<User> users = null;
        Callable<List<User>> callableGetUsersTask = () -> userDao.getUsers();

        CompletionService<List<User>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(callableGetUsersTask);
        Future<List<User>> future = service.take();
        return future.get();
/*
        Future<List<User>> result = AppDatabase.databaseExecutor.submit(callableGetUsersTask);
        while (!result.isDone()) {
            try {
                users= result.get();
                Log.d("UserRepository.findUser", "Task is done, return user: " + users);
                return users;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            //Sleep for 10 ms second
            try {
                Thread.sleep(100000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return users;

 */
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public User findUser(String username) throws Exception {
        Log.d("UserRepository.findUser", "user: " + username);
        Callable<User> callableFindUserTask = () -> {
            Log.d("UserRepository.findUser", "current users: " + userDao.getUsers());
            return userDao.getByUsername(username);
        };
        Future<User> result = AppDatabase.databaseExecutor.submit(callableFindUserTask);
        while (!result.isDone()) {
            try {
                User u = result.get();
                Log.d("UserRepository.findUser", "Task is done, return user: " + u);
                return u;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            //Sleep for 10 ms second
            try {
                Thread.sleep(100000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void insert(User user) {
        Log.d("UserRepository.insert", "create new user. user: " + user.getUsername());
        AppDatabase.databaseExecutor.execute(() -> userDao.insert(user));
    }

    public void delete(User user) {
        AppDatabase.databaseExecutor.execute(() -> {
            userDao.delete(user);
            Log.d("CredentialRepository.save", "deleted, id=" + user.getUsername());
        });
    }

}
