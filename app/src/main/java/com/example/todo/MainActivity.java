package com.example.todo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.todo.WeekPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> notifPerm;

    private ViewPager2 pager;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = findViewById(R.id.pager);
        TabLayout tabs = findViewById(R.id.tabLayout);
        pager.setAdapter(new WeekPagerAdapter(this));
        new TabLayoutMediator(tabs, pager, (tab, position) -> {
            String[] names = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
            tab.setText(names[position]);
        }).attach();

        // Permission (Android 13+)
        notifPerm = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> { /* no-op */ }
        );
        requestNotifPermissionIfNeeded();

        // Handle notification tap (cold start)
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // update the intent returned by getIntent()
        // Handle notification tap when activity is already running
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;
        boolean openToday = intent.getBooleanExtra("open_today", false);
        String iso = intent.getStringExtra("target_date");
        if (openToday && iso != null) {
            try {
                LocalDate target = LocalDate.parse(iso); // yyyy-MM-dd
                // Monday=0 ... Sunday=6
                int index = dayOfWeekToIndex(target.getDayOfWeek());
                if (index >= 0 && index < 7) {
                    pager.post(() -> pager.setCurrentItem(index, false));
                }
            } catch (Exception ignore) { /* safe parse */ }
        }
    }

    private int dayOfWeekToIndex(DayOfWeek dow) {
        // DayOfWeek.MONDAY.getValue() == 1 ... SUNDAY == 7
        return dow.getValue() - 1;
    }

    private void requestNotifPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notifPerm.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
