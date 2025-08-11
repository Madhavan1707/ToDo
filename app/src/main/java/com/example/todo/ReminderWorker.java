package com.example.todo;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReminderWorker extends Worker {
    private final TaskRepository repo;

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        repo = new TaskRepository(context);
    }

    @NonNull @Override
    public Result doWork() {
        // Only between 08:00 and 23:59
        int hour = LocalTime.now().getHour();
        if (hour < 8 || hour > 23) return Result.success();

        LocalDate today = LocalDate.now();
        int remaining = repo.countRemainingNow(today);
        int total = repo.countTotalNow(today);
        int points = (total - remaining) * 10;

        if (remaining > 0) {
            // Build an intent that opens MainActivity at today's tab
            Context ctx = getApplicationContext();
            Intent intent = new Intent(ctx, MainActivity.class)
                    .putExtra("target_date", today.toString()) // ISO yyyy-MM-dd
                    .putExtra("open_today", true)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            // Proper back stack so Back goes home, not to a blank screen
            int requestCode = 1002;
            var pending = TaskStackBuilder.create(ctx)
                    .addNextIntentWithParentStack(intent)
                    .getPendingIntent(requestCode,
                            android.os.Build.VERSION.SDK_INT >= 23
                                    ? (android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE)
                                    : android.app.PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, MyApp.CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_notify_more)
                    .setContentTitle("Tasks remaining today")
                    .setContentText(points + " pts • " + remaining + " remaining of " + total)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pending)     // 👈 opens app to today
                    .setAutoCancel(true);

            NotificationManagerCompat.from(ctx).notify(1001, b.build());
        }
        return Result.success();
    }
}
