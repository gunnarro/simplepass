package com.gunnarro.android.simplepass.ui.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.simplepass.config.AppDatabase;
import com.gunnarro.android.simplepass.domain.entity.Message;
import com.gunnarro.android.simplepass.repository.MessageRepository;
import com.gunnarro.android.simplepass.validator.CustomPasswordValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 * We use AndroidViewModel because we can pass the application context in the constructor.
 */
public class MessageViewModel extends AndroidViewModel {

    private final MessageRepository messageRepository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    private final LiveData<List<Message>> messages;

    public MessageViewModel(@NonNull Application application) throws GeneralSecurityException, IOException {
        super(application);
        messageRepository = new MessageRepository(AppDatabase.getDatabaseEncrypted(application).messageDao());
        // FIXME hardcoded userId
        messages = messageRepository.getAllMessages(1L);
    }

    public LiveData<List<Message>> getMessageLiveData() {
        return messages;
    }

    public void save(Message message) throws Exception {
        Log.d("MessageViewModel.save" , "save: " + message);
        messageRepository.save(message);
    }

    public void delete(Message message) {
        Log.d("MessageViewModel.delete" , "save: " + message);
        messageRepository.delete(message);
    }

    public void messageDataChanged(String tag, String text) {
        if (!isTagValid(tag)) {
          //  credentialFormState.setValue(new CredentialFormState(R.string.invalid_username, null, null, null));
        } else if (!isTextValid(text)) {
          //  credentialFormState.setValue(new CredentialFormState(null, 23, null, null));
        }
    }

    // A placeholder encryptionKey validation check
    private List<String> isEncryptionKeyValid(String encryptionKey) {
        return new CustomPasswordValidator().passwordStrength(encryptionKey);
    }

    private boolean isTagValid(String tag) {
        return tag != null && tag.trim().length() > 1;
    }

    private boolean isTextValid(String text) {
        return text != null;
    }
}
