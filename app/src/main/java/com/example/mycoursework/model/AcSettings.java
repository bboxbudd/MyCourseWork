package com.example.mycoursework.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "ac_settings",
        foreignKeys = @ForeignKey(
                entity = Device.class,
                parentColumns = "id",
                childColumns = "deviceId",
                onDelete = ForeignKey.CASCADE
        )
)
public class AcSettings {
    @PrimaryKey
    @NonNull
    private String deviceId;
    private int temperature;

    public AcSettings(@NonNull String deviceId, int temperature) {
        this.deviceId = deviceId;
        this.temperature = temperature;
    }

    @NonNull
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(@NonNull String deviceId) {
        this.deviceId = deviceId;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
