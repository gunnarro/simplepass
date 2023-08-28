package com.gunnarro.android.simplepass.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.entity.Message;
import com.gunnarro.android.simplepass.exception.SimpleCredStoreApplicationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

public class MessageRepository {

    private final MessageDao messageDao;

    public MessageRepository(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Message>> getAllMessages(Long userId) {
        Log.d("MessageRepository.getAllMessages", "refresh live data in fragment");
        return messageDao.getAll(userId);
    }

    public List<Message> getMessages(Long userId) {
        Log.d("MessageRepository.getAllMessages", "refresh live data in fragment");
        return messageDao.getMessages(userId);
    }

    public Message getMessage(Long id) {
        return messageDao.getById(id);
    }

    public void delete(Message credential) {
        AppDatabase.databaseExecutor.execute(() -> {
            messageDao.delete(credential);
            Log.d("MessageRepository.delete", "deleted, id=" + credential.getId());
        });
    }

    public Long save(final Message message) {
        Long id;
        try {
            if (message.getId() == null) {
                message.setCreatedDate(LocalDateTime.now());
                message.setLastModifiedDate(LocalDateTime.now());
                id = insertMessage(message);
            } else {
                message.setLastModifiedDate(LocalDateTime.now());
                Integer i = updateMessage(message);
                id = Long.valueOf(i);
            }
            return id;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new SimpleCredStoreApplicationException("Error saving message!", e.getMessage(), e.getCause());
        }
    }

    private Long insertMessage(Message message) throws InterruptedException, ExecutionException {
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> messageDao.insert(message));
        Future<Long> future = service.take();
        return future.get();
    }

    private Integer updateMessage(Message message) throws InterruptedException, ExecutionException {
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> messageDao.update(message));
        Future<Integer> future = service.take();
        return future.get();
    }
}
