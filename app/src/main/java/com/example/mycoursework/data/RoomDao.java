package com.example.mycoursework.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mycoursework.model.Room;

import java.util.List;

@Dao
public interface RoomDao {
    @Query("SELECT * FROM rooms WHERE ownerUsername = :username")
    LiveData<List<Room>> getRoomsForUser(String username);

    @Query("SELECT * FROM rooms WHERE ownerUsername = :username AND name = :roomName LIMIT 1")
    Room getRoomByNameAndUser(String username, String roomName);

    @Query("SELECT name FROM rooms WHERE id = :roomId LIMIT 1")
    LiveData<String> getRoomNameById(String roomId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Room room);

    @Query("DELETE FROM rooms WHERE id = :roomId")
    void deleteById(String roomId);
}
