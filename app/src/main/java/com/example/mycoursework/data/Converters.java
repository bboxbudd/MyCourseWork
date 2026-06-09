package com.example.mycoursework.data;

import androidx.room.TypeConverter;

import com.example.mycoursework.model.Device;
import com.example.mycoursework.model.User;

public class Converters {
    @TypeConverter
    public static String fromRole(User.Role role) {
        return role == null ? null : role.name();
    }

    @TypeConverter
    public static User.Role toRole(String role) {
        return role == null ? null : User.Role.valueOf(role);
    }

    @TypeConverter
    public static String fromType(Device.Type type) {
        return type == null ? null : type.name();
    }

    @TypeConverter
    public static Device.Type toType(String type) {
        return type == null ? null : Device.Type.valueOf(type);
    }
}
