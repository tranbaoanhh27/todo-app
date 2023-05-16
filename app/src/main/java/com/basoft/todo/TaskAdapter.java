package com.basoft.todo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private ArrayList<TaskTodo> tasks;
    Context context;

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
        holder.setViewsIsDoneState(task.isDone());
        holder.deadlineTextView.setText(task.getDeadlineString(context));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private TextView titleTextView, deadlineTextView, deadlineLabelTextView;
        private CheckBox checkBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            findViews(itemView);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateOnCheck(isChecked));
            itemView.setOnClickListener(TaskViewHolder.this);
            itemView.setOnLongClickListener(TaskViewHolder.this);
        }

        public void updateOnCheck(boolean isChecked) {
            setViewsIsDoneState(isChecked);
            int position = getAdapterPosition();
            DataManager dataManager = DataManager.getInstance();
            dataManager.setTaskDone(position, isChecked);
            ((MainActivity) TaskAdapter.this.context).notifyAdapter();
        }

        private void setViewsIsDoneState(boolean isDone) {
            checkBox.setChecked(isDone);
            if (isDone) {
                titleTextView.setPaintFlags(titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                titleTextView.setTextColor(Color.GRAY);
                deadlineTextView.setPaintFlags(deadlineTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                deadlineTextView.setTextColor(Color.GRAY);
                deadlineLabelTextView.setPaintFlags(deadlineTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                deadlineLabelTextView.setTextColor(Color.GRAY);
            } else {
                titleTextView.setPaintFlags(titleTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                titleTextView.setTextColor(Color.BLACK);
                deadlineTextView.setPaintFlags(deadlineTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                deadlineTextView.setTextColor(Color.BLACK);
                deadlineLabelTextView.setPaintFlags(deadlineTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                deadlineLabelTextView.setTextColor(Color.BLACK);
            }
        }

        private void findViews(View itemView) {
            titleTextView = itemView.findViewById(R.id.task_item_title);
            deadlineTextView = itemView.findViewById(R.id.task_item_deadline);
            deadlineLabelTextView = itemView.findViewById(R.id.task_item_deadline_label);
            checkBox = itemView.findViewById(R.id.task_item_checkbox);
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

            DialogInterface.OnClickListener onCancelListener = (dialog, which) -> {
                // Just close the dialog
            };

            DialogInterface.OnClickListener onConfirmListener = (dialog, which) -> {
                // Delete the task
                DataManager dataManager = DataManager.getInstance();
                int position = getAdapterPosition();
                dataManager.deleteTask(position);
                TaskAdapter.this.notifyItemRemoved(position);
                TaskAdapter.this.notifyItemRangeChanged(position, TaskAdapter.this.tasks.size());

                // TODO: Un-schedule the notification
            };

            dialogBuilder
                    .setTitle(R.string.delete_task_question)
                    .setMessage(R.string.are_you_sure_that_you_want_to_delete_this_task)
                    .setNegativeButton(R.string.cancel, onCancelListener)
                    .setPositiveButton(R.string.yes, onConfirmListener);

            AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            return true;
        }

        @Override
        public void onClick(View v) {
            DataManager dataManager = DataManager.getInstance();
            updateOnCheck(!dataManager.getTasks().get(getAdapterPosition()).isDone());
        }
    }
}
