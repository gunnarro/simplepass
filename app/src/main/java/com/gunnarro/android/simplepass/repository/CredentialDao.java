package com.gunnarro.android.simplepass.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.simplepass.domain.entity.Credential;

import java.util.List;

@Dao
public interface CredentialDao {

    @Query("SELECT * FROM credential WHERE id = :id")
    Credential getById(long id);

    @Query("SELECT * FROM credential WHERE username = :username")
    Credential getByUsername(String username);

    @Query("SELECT * FROM credential WHERE fk_user_id =:userId ORDER BY system ASC")
    LiveData<List<Credential>> getAll(Long userId);

    /**
     * @param credential credential to be inserted
     * @return the id of the inserted credential row
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(Credential credential);

    /**
     * @param credential updated credential
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Credential credential);

    /**
     * @param credential to be deleted
     */
    @Delete
    void delete(Credential credential);

}
