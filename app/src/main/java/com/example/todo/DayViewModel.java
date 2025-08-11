package com.example.todo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todo.Task;
import com.example.todo.TaskRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class DayViewModel extends AndroidViewModel {

    public static class UiState {
        public final int total;
        public final int remaining;
        public final int points;
        public final int done;
        public final boolean animate; // only when done changes and total is same

        UiState(int total, int remaining, int points, int done, boolean animate) {
            this.total = total;
            this.remaining = remaining;
            this.points = points;
            this.done = done;
            this.animate = animate;
        }
    }

    private final TaskRepository repo;
    private final LocalDate date;
    private final LiveData<List<Task>> tasks;
    private final MutableLiveData<UiState> _ui = new MutableLiveData<>();
    public final LiveData<UiState> ui = _ui;

    private int lastTotal = -1;
    private int lastDone = -1;

    public DayViewModel(@NonNull Application app, @NonNull LocalDate date) {
        super(app);
        this.date = date;
        this.repo = new TaskRepository(app);

        // seed defaults
        List<String> defaults = Arrays.asList("Creatine", "10k steps", "Food", "Workout", "Clothes");
        repo.seedDefaultsIfEmpty(date, defaults);

        // observe tasks and emit a single UiState
        this.tasks = repo.observeTasksForDate(date);
        this.tasks.observeForever(list -> {
            int total = list == null ? 0 : list.size();
            int done = 0;
            if (list != null) for (Task t : list) if (t.isDone) done++;
            int remaining = total - done;
            int points = done * 10;

            boolean animate = (lastTotal == total) && (lastDone != -1) && (lastDone != done);
            _ui.setValue(new UiState(total, remaining, points, done, animate));

            lastTotal = total;
            lastDone = done;
        });
    }

    public LiveData<List<Task>> getTasks() { return tasks; }

    public void addTask(String title) { repo.addTask(date, title, false); }

    public void toggle(Task t) { repo.toggleDone(t); }
}
