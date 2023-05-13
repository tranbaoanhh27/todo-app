package com.basoft.todo;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private ArrayList<TaskTodo> tasks;
    private Context context;

    public TaskAdapter(ArrayList<TaskTodo> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item_view, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskTodo task = tasks.get(position);
        holder.titleTextView.setText(task.getTitle());
        if (task.isDone()) {
            holder.titleTextView.setPaintFlags(
                    holder.titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.checkBox.setChecked(true);
        }
        holder.deadlineTextView.setText(task.getDeadlineString(context));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView, deadlineTextView;
        private CheckBox checkBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            findViews(itemView);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DataManager dataManager = DataManager.getInstance();
                    if (isChecked) {
                        titleTextView.setPaintFlags(titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        dataManager.setTaskDone(getAdapterPosition(), true);
                    }
                    else {
                        titleTextView.setPaintFlags(titleTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                        dataManager.setTaskDone(getAdapterPosition(), false);
                    }
                }
            });
        }

        private void findViews(View itemView) {
            titleTextView = itemView.findViewById(R.id.task_item_title);
            deadlineTextView = itemView.findViewById(R.id.task_item_deadline);
            checkBox = itemView.findViewById(R.id.task_item_checkbox);
        }
    }
}
