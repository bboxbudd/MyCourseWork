package com.example.mycoursework.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mycoursework.model.AcSettings;

@Dao
public interface AcSettingsDao {
    @Query("SELECT * FROM ac_settings WHERE deviceId = :deviceId LIMIT 1")
    LiveData<AcSettings> getSettingsForDevice(String deviceId);

    @Query("SELECT * FROM ac_settings WHERE deviceId = :deviceId LIMIT 1")
    AcSettings getSettingsForDeviceSync(String deviceId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AcSettings settings);

    @Update
    void update(AcSettings settings);
}
