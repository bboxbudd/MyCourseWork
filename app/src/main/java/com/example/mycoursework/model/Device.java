package com.example.mycoursework.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.mycoursework.R;

import java.util.Objects;
import java.util.UUID;

public class Device implements Parcelable {
    public enum Type {
        LIGHT, AC, SOCKET
    }

    private final String id;
    private String name;
    private String room;
    private Type type;
    private boolean isEnabled;
    private int temperature;
    private int timerMinutes;
    private long timerStartTime;
    private boolean isTimerActive;
    private int imageResId;

    public Device(String name, String room, Type type) {
        this(name, room, type, getDefaultImage(type));
    }

    public Device(String name, String room, Type type, int imageResId) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.room = room;
        this.type = type;
        this.isEnabled = false;
        this.temperature = 22;
        this.timerMinutes = 0;
        this.timerStartTime = 0;
        this.isTimerActive = false;
        this.imageResId = imageResId;
    }

    private Device(String id, String name, String room, Type type, boolean isEnabled, 
                   int temperature, int timerMinutes, long timerStartTime, 
                   boolean isTimerActive, int imageResId) {
        this.id = id;
        this.name = name;
        this.room = room;
        this.type = type;
        this.isEnabled = isEnabled;
        this.temperature = temperature;
        this.timerMinutes = timerMinutes;
        this.timerStartTime = timerStartTime;
        this.isTimerActive = isTimerActive;
        this.imageResId = imageResId;
    }

    public Device copy() {
        return new Device(id, name, room, type, isEnabled, temperature, 
                          timerMinutes, timerStartTime, isTimerActive, imageResId);
    }

    private static int getDefaultImage(Type type) {
        switch (type) {
            case AC: return R.drawable.img_ac;
            case SOCKET: return R.drawable.img_plug;
            case LIGHT: return R.drawable.img_lamp;
            default: return R.drawable.img_lamp;
        }
    }

    protected Device(Parcel in) {
        id = in.readString();
        name = in.readString();
        room = in.readString();
        type = Type.valueOf(in.readString());
        isEnabled = in.readByte() != 0;
        temperature = in.readInt();
        timerMinutes = in.readInt();
        timerStartTime = in.readLong();
        isTimerActive = in.readByte() != 0;
        imageResId = in.readInt();
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }
    public int getTemperature() { return temperature; }
    public void setTemperature(int temperature) { this.temperature = temperature; }
    public int getTimerMinutes() { return timerMinutes; }
    public void setTimerMinutes(int timerMinutes) { this.timerMinutes = timerMinutes; }
    public long getTimerStartTime() { return timerStartTime; }
    public void setTimerStartTime(long timerStartTime) { this.timerStartTime = timerStartTime; }
    public boolean isTimerActive() { return isTimerActive; }
    public void setTimerActive(boolean timerActive) { isTimerActive = timerActive; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(room);
        dest.writeString(type.name());
        dest.writeByte((byte) (isEnabled ? 1 : 0));
        dest.writeInt(temperature);
        dest.writeInt(timerMinutes);
        dest.writeLong(timerStartTime);
        dest.writeByte((byte) (isTimerActive ? 1 : 0));
        dest.writeInt(imageResId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return isEnabled == device.isEnabled && temperature == device.temperature && 
               timerMinutes == device.timerMinutes && timerStartTime == device.timerStartTime && 
               isTimerActive == device.isTimerActive && imageResId == device.imageResId && 
               Objects.equals(id, device.id) && Objects.equals(name, device.name) && 
               Objects.equals(room, device.room) && type == device.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, room, type, isEnabled, temperature, 
                            timerMinutes, timerStartTime, isTimerActive, imageResId);
    }
}
