package com.example.todo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.todo.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.time.LocalDate;

public class DayFragment extends Fragment {
    private static final String ARG_DATE = "arg_date_iso";

    public static DayFragment newInstance(LocalDate date) {
        Bundle b = new Bundle();
        b.putString(ARG_DATE, date.toString());
        DayFragment f = new DayFragment();
        f.setArguments(b);
        return f;
    }

    private DayViewModel vm;
    private TaskAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_day, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // Views FIRST
        TextView tvPoints = v.findViewById(R.id.tvPoints);
        TextView tvRemaining = v.findViewById(R.id.tvRemaining);
        CircularProgressIndicator ring = v.findViewById(R.id.progressRing);
        LottieAnimationView confetti = v.findViewById(R.id.confetti);
        RecyclerView rv = v.findViewById(R.id.recycler);

        // 🔒 Force determinate mode to prevent the 4-dashes indeterminate rendering
        ring.setIndeterminate(false);

        // VM
        String iso = getArguments().getString(ARG_DATE);
        LocalDate date = LocalDate.parse(iso);
        vm = new ViewModelProvider(
                this,
                new DayVMFactory(requireActivity().getApplication(), date)
        ).get(DayViewModel.class);

        // Recycler
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TaskAdapter(vm);
        rv.setAdapter(adapter);
        RecyclerView.ItemAnimator anim = rv.getItemAnimator();
        if (anim instanceof androidx.recyclerview.widget.SimpleItemAnimator) {
            ((androidx.recyclerview.widget.SimpleItemAnimator) anim).setSupportsChangeAnimations(false);
        }

        // Single, coherent UI stream
        final int[] prevRemaining = { -1 };
        vm.ui.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            // Labels
            tvPoints.setText(state.points + " pts");
            tvRemaining.setText(state.remaining + " remaining of " + state.total);

            // Max only when total changes; never animate on max change
            int newMax = Math.max(1, state.total);
            if (ring.getMax() != newMax) {
                ring.setIndeterminate(false); // re-assert, just in case
                ring.setMax(newMax);
                ring.setProgressCompat(Math.min(state.done, newMax), false);
            } else {
                ring.setProgressCompat(Math.min(state.done, newMax), state.animate);
            }

            // Confetti when hitting all done (not on first draw)
            if (state.total > 0 && state.remaining == 0 && prevRemaining[0] > 0) {
                confetti.setVisibility(View.VISIBLE);
                confetti.playAnimation();
                confetti.addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(Animator animation) {
                        confetti.setVisibility(View.GONE);
                        confetti.removeAllAnimatorListeners();
                    }
                });
            }
            prevRemaining[0] = state.remaining;
        });

        // List
        vm.getTasks().observe(getViewLifecycleOwner(), adapter::submitList);

        v.findViewById(R.id.fabAdd).setOnClickListener(view -> showAddDialog());
    }

    private void showAddDialog() {
        EditText input = new EditText(requireContext());
        input.setHint("Task title");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(requireContext())
                .setTitle("New Task")
                .setView(input)
                .setPositiveButton("Add", (d, w) -> {
                    String title = input.getText().toString().trim();
                    if (!title.isEmpty()) vm.addTask(title);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Adapter (unchanged except stable IDs + contents include completedAt)
    static class TaskAdapter extends ListAdapter<Task, TaskVH> {
        private final DayViewModel vm;
        TaskAdapter(DayViewModel vm) {
            super(new DiffUtil.ItemCallback<Task>() {
                @Override public boolean areItemsTheSame(@NonNull Task a, @NonNull Task b) { return a.id == b.id; }
                @Override public boolean areContentsTheSame(@NonNull Task a, @NonNull Task b) {
                    boolean sameTitle = a.title.equals(b.title);
                    boolean sameDone = a.isDone == b.isDone;
                    boolean sameCompletedAt = (a.completedAt == null && b.completedAt == null)
                            || (a.completedAt != null && a.completedAt.equals(b.completedAt));
                    return sameTitle && sameDone && sameCompletedAt;
                }
            });
            this.vm = vm;
            setHasStableIds(true);
        }
        @Override public long getItemId(int position) {
            Task t = getItem(position);
            return t == null ? RecyclerView.NO_ID : t.id;
        }
        @NonNull @Override public TaskVH onCreateViewHolder(@NonNull ViewGroup p, int v) {
            View item = LayoutInflater.from(p.getContext()).inflate(R.layout.row_task, p, false);
            return new TaskVH(item);
        }
        @Override public void onBindViewHolder(@NonNull TaskVH h, int pos) {
            Task t = getItem(pos);
            h.bind(t, () -> vm.toggle(t));
        }
    }
}
