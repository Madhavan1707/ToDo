package com.example.todo;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {
    private final TaskDao dao;
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    public TaskRepository(Context ctx) {
        dao = AppDatabase.get(ctx).taskDao();
    }

    public LiveData<List<Task>> observeTasksForDate(LocalDate date) {
        return dao.observeTasksForDate(date.format(ISO));
    }

    public void addTask(LocalDate date, String title, boolean isDefault) {
        io.execute(() -> dao.insert(new Task(date.format(ISO), title, isDefault)));
    }

    public void toggleDone(Task t) {
        io.execute(() -> {
            t.isDone = !t.isDone;
            t.completedAt = t.isDone ? System.currentTimeMillis() : null;
            dao.update(t);
        });
    }

    public void seedDefaultsIfEmpty(LocalDate date, List<String> defaults) {
        io.execute(() -> {
            String d = date.format(ISO);
            if (dao.countForDateNow(d) == 0) {
                List<Task> list = new ArrayList<>();
                for (String name : defaults) list.add(new Task(d, name, true));
                dao.insertAll(list);
            }
        });
    }
    public void carryOver(LocalDate from) {
        io.execute(() -> {
            String d = from.format(ISO);
            String n = from.plusDays(1).format(ISO);
            List<Task> open = dao.getOpenTasksNow(d);
            if (open.isEmpty()) return;
            for (Task t : open) {
                Task clone = new Task(n, t.title, t.isDefault);
                dao.insert(clone);
            }
        });
    }

    public int countRemainingNow(LocalDate date) { // for Worker (call on background)
        return dao.countRemainingNow(date.format(ISO));
    }
    public int countTotalNow(LocalDate date) {
        return dao.countTotalNow(date.format(ISO));
    }
}
