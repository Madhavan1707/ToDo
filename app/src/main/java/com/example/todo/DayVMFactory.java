package com.example.todo;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
public class DayVMFactory implements ViewModelProvider.Factory {
    private final Application app;
    private final LocalDate date;
    public DayVMFactory(@NonNull Application app, @NonNull LocalDate date) {
        this.app = app; this.date = date;
    }
    @NonNull @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> cls) {
        if (cls.isAssignableFrom(DayViewModel.class)) {
            return (T) new DayViewModel(app, date);
        }
        throw new IllegalArgumentException("Unknown VM class");
    }
}
