package com.example.todo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("""
SELECT * FROM tasks
WHERE date=:date
ORDER BY isDone ASC,
         CASE WHEN isDone=0 THEN createdAt END ASC,
         CASE WHEN isDone=1 THEN completedAt END ASC,
         id ASC
""")
    LiveData<List<Task>> observeTasksForDate(String date);

    @Query("SELECT COUNT(*) FROM tasks WHERE date=:date AND isDone=0")
    int countRemainingNow(String date);

    @Query("SELECT COUNT(*) FROM tasks WHERE date=:date")
    int countTotalNow(String date);

    @Query("SELECT COUNT(*) FROM tasks WHERE date=:date")
    int countForDateNow(String date);
    @Query("SELECT * FROM tasks WHERE date=:date AND isDone=0 ORDER BY createdAt ASC")
    List<Task> getOpenTasksNow(String date);


    @Insert
    long insert(Task t);

    @Insert
    void insertAll(List<Task> tasks);

    @Update
    void update(Task t);
}
