package com.example.todo;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;

public class WeekPagerAdapter extends FragmentStateAdapter {
    private final LocalDate monday;

    public WeekPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
        monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    @NonNull @Override public Fragment createFragment(int position) {
        return DayFragment.newInstance(monday.plusDays(position));
    }

    @Override public int getItemCount() { return 7; }
}
