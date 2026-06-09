package com.example.mycoursework.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mycoursework.model.Device;

import java.util.List;

@Dao
public interface DeviceDao {
    @Query("SELECT devices.* FROM devices INNER JOIN rooms ON devices.roomId = rooms.id WHERE rooms.ownerUsername = :username")
    LiveData<List<Device>> getDevicesForUser(String username);

    @Query("SELECT * FROM devices WHERE id = :deviceId LIMIT 1")
    Device getDeviceById(String deviceId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Device device);

    @Update
    void update(Device device);

    @Query("DELETE FROM devices WHERE id = :deviceId")
    void deleteById(String deviceId);
}
