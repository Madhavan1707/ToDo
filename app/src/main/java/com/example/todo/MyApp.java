package com.example.todo;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.todo.MidnightWorker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class MyApp extends Application {
    public static final String CHANNEL_ID = "tasks_reminders";
    private static final String UNIQUE_HOURLY = "hourly_reminder";
    private static final String UNIQUE_MIDNIGHT = "midnight_carryover";

    @Override public void onCreate() {
        super.onCreate();

        // Force light mode (your preference)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        createChannel();
        scheduleHourlyReminders();      // back to 1 hour
        scheduleMidnightCarryOver();    // 00:05 every day
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID,
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(ch);
        }
    }

//    private void scheduleHourlyReminders() {
//        long minutesToNextHour = 60 - LocalDateTime.now().getMinute(); // first run next hour boundary
//
//        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(ReminderWorker.class, 1, TimeUnit.HOURS)
//                .setInitialDelay(minutesToNextHour, TimeUnit.MINUTES)
//                .build();
//
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//                UNIQUE_HOURLY,
//                ExistingPeriodicWorkPolicy.KEEP, // keep existing schedule if present
//                req
//        );
//    }

    private void scheduleHourlyReminders() {
        // For testing: 15-minute interval
        long minutesToNext = 15 - (LocalDateTime.now().getMinute() % 15);

        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(ReminderWorker.class, 15, TimeUnit.MINUTES)
                .setInitialDelay(minutesToNext, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                UNIQUE_HOURLY,
                ExistingPeriodicWorkPolicy.KEEP,
                req
        );
    }


    private void scheduleMidnightCarryOver() {
        long minutes = minutesUntil(0, 5); // 00:05 next occurrence
        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(MidnightWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(minutes, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                UNIQUE_MIDNIGHT,
                ExistingPeriodicWorkPolicy.KEEP,
                req
        );
    }

    private long minutesUntil(int targetHour, int targetMinute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.withHour(targetHour).withMinute(targetMinute).withSecond(0).withNano(0);
        if (!next.isAfter(now)) next = next.plusDays(1);
        return Duration.between(now, next).toMinutes();
    }
}
