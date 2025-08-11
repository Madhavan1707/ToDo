package com.example.todo;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.Task;

class TaskVH extends RecyclerView.ViewHolder {
    private final CheckBox chk;
    private final TextView txt;

    private final int colorNormal;
    private final int colorDone;
    private static final float ALPHA_DONE = 0.45f; // fade level when done
    private static final int ANIM_MS = 160;

    TaskVH(@NonNull View itemView) {
        super(itemView);
        chk = itemView.findViewById(R.id.chk);
        txt = itemView.findViewById(R.id.txt);
        colorNormal = ContextCompat.getColor(itemView.getContext(), R.color.taskText);
        colorDone   = ContextCompat.getColor(itemView.getContext(), R.color.taskDone);
    }

    void bind(Task t, Runnable onToggle) {
        txt.setText(t.title);

        // detach listener to avoid re-trigger
        chk.setOnCheckedChangeListener(null);
        chk.setChecked(t.isDone);

        // apply visual state (with tiny animation when binding after a toggle)
        applyVisualState(t.isDone, true);

        chk.setOnCheckedChangeListener((b, isChecked) -> {
            onToggle.run();                 // update DB
            applyVisualState(isChecked, false); // immediate UI feedback
        });
    }

    private void applyVisualState(boolean done, boolean instant) {
        float targetAlpha = done ? ALPHA_DONE : 1f;
        int   targetColor = done ? colorDone : colorNormal;

        if (instant) {
            txt.setAlpha(targetAlpha);
            txt.setTextColor(targetColor);
            return;
        }

        // Soft animate both alpha and color
        txt.animate().alpha(targetAlpha).setDuration(ANIM_MS).start();

        final int start = ((TextView) txt).getCurrentTextColor();
        ValueAnimator va = ValueAnimator.ofObject(new ArgbEvaluator(), start, targetColor);
        va.setDuration(ANIM_MS);
        va.addUpdateListener(a -> txt.setTextColor((int) a.getAnimatedValue()));
        va.start();
    }
}
