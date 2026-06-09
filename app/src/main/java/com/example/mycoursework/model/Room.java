package com.example.mycoursework.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(
        tableName = "rooms",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "username",
                childColumns = "ownerUsername",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("ownerUsername")}
)
public class Room {
    @PrimaryKey
    @NonNull
    private final String id;
    private final String name;
    private final String ownerUsername;

    @Ignore
    public Room(String name, String ownerUsername) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.ownerUsername = ownerUsername;
    }

    public Room(@NonNull String id, String name, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.ownerUsername = ownerUsername;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }
}
