package com.example.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(tableName = "tasks", indices = {@Index("date")})
public class Task {
    @PrimaryKey(autoGenerate = true) public long id;
    @NonNull public String date; // yyyy-MM-dd
    @NonNull public String title;
    public boolean isDone;
    public boolean isDefault;
    public long createdAt;            // epoch millis
    @Nullable public Long completedAt; // epoch millis or null

    public Task(@NonNull String date, @NonNull String title, boolean isDefault) {
        this.date = date;
        this.title = title;
        this.isDefault = isDefault;
        this.createdAt = System.currentTimeMillis();
        this.isDone = false;
        this.completedAt = null;
    }
}
