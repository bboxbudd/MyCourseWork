package com.example.mycoursework.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User implements Parcelable {
    public enum Role {
        ADMIN, USER
    }

    @PrimaryKey
    @NonNull
    private final String username;
    private final String password;
    private final Role role;

    public User(@NonNull String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    protected User(Parcel in) {
        username = in.readString();
        password = in.readString();
        role = Role.valueOf(in.readString());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @NonNull
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(role.name());
    }
}
