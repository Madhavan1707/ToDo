package com.example.todo;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.todo.TaskRepository;

import java.time.LocalDate;

public class MidnightWorker extends Worker {
    public MidnightWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull @Override
    public Result doWork() {
        TaskRepository repo = new TaskRepository(getApplicationContext());
        // carry over yesterday's remaining to today
        repo.carryOver(LocalDate.now().minusDays(1));
        return Result.success();
    }
}
