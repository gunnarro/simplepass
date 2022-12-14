package com.gunnarro.android.simplepass.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.exception.SimpleCredStoreApplicationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

public class CredentialRepository {

    private final CredentialDao credentialDao;

    public CredentialRepository(CredentialDao credentialDao) {
        this.credentialDao = credentialDao;
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Credential>> getAllCredentials(Long userId) {
        Log.d("CredentialRepository.getAllCredentials", "refresh live data in fragment");
        return credentialDao.getAll(userId);
    }

    public List<Credential> getCredentials(Long userId) {
        Log.d("CredentialRepository.getAllCredentials", "refresh live data in fragment");
        return credentialDao.getCredentials(userId);
    }

    public Credential getCredential(Long id) {
        return credentialDao.getById(id);
    }

    public void delete(Credential credential) {
        AppDatabase.databaseExecutor.execute(() -> {
            credentialDao.delete(credential);
            Log.d("CredentialRepository.delete", "deleted, id=" + credential.getId());
        });
    }

    public Long save(final Credential credential) {
        Long id;
        try {
            if (credential.getId() == null) {
                credential.setCreatedDate(LocalDateTime.now());
                credential.setLastModifiedDate(LocalDateTime.now());
                id = insertCredential(credential);
            } else {
                credential.setLastModifiedDate(LocalDateTime.now());
                Integer i = updateCredential(credential);
                id = Long.valueOf(i);
            }
            return id;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new SimpleCredStoreApplicationException("Error saving credential!", e.getMessage(), e.getCause());
        }
    }

    private Long insertCredential(Credential credential) throws InterruptedException, ExecutionException {
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> credentialDao.insert(credential));
        Future<Long> future = service.take();
        return future.get();
    }

    private Integer updateCredential(Credential credential) throws InterruptedException, ExecutionException {
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> credentialDao.update(credential));
        Future<Integer> future = service.take();
        return future.get();
    }
}
