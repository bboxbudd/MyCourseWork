package com.example.mycoursework.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.mycoursework.R;

import java.util.Objects;
import java.util.UUID;

@Entity(
        tableName = "devices",
        foreignKeys = @ForeignKey(
                entity = Room.class,
                parentColumns = "id",
                childColumns = "roomId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("roomId")}
)
public class Device implements Parcelable {
    public enum Type {
        LIGHT, AC, SOCKET
    }

    @PrimaryKey
    @NonNull
    private final String id;
    private final String roomId;
    private String name;
    private Type type;
    private boolean isEnabled;
    private int imageResId;
    private Integer temperature; // Используем Integer для возможности null у ламп и розеток

    @Ignore
    public Device(String name, String roomId, Type type) {
        this(UUID.randomUUID().toString(), roomId, name, type, false, getDefaultImage(type), type == Type.AC ? 22 : null);
    }

    @Ignore
    public Device(String name, String roomId, Type type, int imageResId) {
        this(UUID.randomUUID().toString(), roomId, name, type, false, imageResId, type == Type.AC ? 22 : null);
    }

    public Device(@NonNull String id, String roomId, String name, Type type, boolean isEnabled, 
                   int imageResId, Integer temperature) {
        this.id = id;
        this.roomId = roomId;
        this.name = name;
        this.type = type;
        this.isEnabled = isEnabled;
        this.imageResId = imageResId;
        this.temperature = temperature;
    }

    public Device copy() {
        return new Device(id, roomId, name, type, isEnabled, imageResId, temperature);
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
        roomId = in.readString();
        name = in.readString();
        type = Type.valueOf(in.readString());
        isEnabled = in.readByte() != 0;
        imageResId = in.readInt();
        if (in.readByte() == 0) {
            temperature = null;
        } else {
            temperature = in.readInt();
        }
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

    @NonNull
    public String getId() { return id; }
    public String getRoomId() { return roomId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public Integer getTemperature() { return temperature; }
    public void setTemperature(Integer temperature) { this.temperature = temperature; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(roomId);
        dest.writeString(name);
        dest.writeString(type.name());
        dest.writeByte((byte) (isEnabled ? 1 : 0));
        dest.writeInt(imageResId);
        if (temperature == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(temperature);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return isEnabled == device.isEnabled && 
               imageResId == device.imageResId && 
               Objects.equals(id, device.id) && Objects.equals(roomId, device.roomId) &&
               Objects.equals(name, device.name) && type == device.type &&
               Objects.equals(temperature, device.temperature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomId, name, type, isEnabled, imageResId, temperature);
    }
}
