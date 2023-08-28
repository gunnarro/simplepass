package com.gunnarro.android.simplepass.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.simplepass.domain.entity.Credential;
import com.gunnarro.android.simplepass.domain.entity.Message;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM message WHERE id = :id")
    Message getById(long id);

    @Query("SELECT * FROM message WHERE fk_user_id =:userId ORDER BY tag ASC")
    LiveData<List<Message>> getAll(Long userId);

    @Query("SELECT * FROM message WHERE fk_user_id =:userId ORDER BY tag ASC")
    List<Message> getMessages(Long userId);

    /**
     * @param message message to be inserted
     * @return the id of the inserted message row
     */
    @Insert
    Long insert(Message message);

    /**
     * @param message updated message
     * @return number of updated row(S), should only be one for this method.
     */
    @Update
    Integer update(Message message);

    /**
     * @param message to be deleted
     */
    @Delete
    void delete(Message message);

}
