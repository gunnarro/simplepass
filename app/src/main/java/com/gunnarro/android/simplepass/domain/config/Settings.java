package com.gunnarro.android.simplepass.domain.config;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings_table")
public class Settings {

    /**
     * Auto generated id
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id;

    /**
     * app auto logout time is seconds
     */
    @ColumnInfo(name = "app_auto_logout_time")
    public Integer appAutoLogoutTime = 120;

    @ColumnInfo(name = "fingerprintLoginEnabled")
    public Boolean fingerPrintLoginEnabled;

}
