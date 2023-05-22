package com.basoft.todo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.updateViews(position);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private TextView titleTextView, deadlineTextView, deadlineLabelTextView, statusTextView;
        private CheckBox checkBox;
        private Intent editTaskIntent;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            findViews(itemView);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateOnCheck(isChecked));
            itemView.setOnLongClickListener(TaskViewHolder.this);
        }

        public void updateOnCheck(boolean isChecked) {
            int position = getAdapterPosition();
            DataManager dataManager = DataManager.getInstance();
            int newPosition = dataManager.setTaskDone(position, isChecked);
            ((MainActivity) TaskAdapter.this.context).notifyItemMoved(position, newPosition);
            updateViews(newPosition);
        }

        public void updateViews(int taskPosition) {
            TaskTodo task = tasks.get(taskPosition);
            if (task != null) {
                titleTextView.setText(task.getTitle());
                deadlineTextView.setText(task.getDeadlineString(TaskAdapter.this.context));
                if (task.isDone()) updateViewsAsDone();
                else {
                    updateViewsAsNotDone();
                    if (task.missedDeadline()) updateViewsAsMissed();
                    else if (task.isUrgent()) updateViewsAsUrgent();
                }
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void updateViewsAsUrgent() {
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setText(R.string.urgent);
            statusTextView.setBackground(context.getDrawable(R.drawable.rounded_yellow_rectangle));
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void updateViewsAsMissed() {
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setText(R.string.missed);
            statusTextView.setBackground(context.getDrawable(R.drawable.rounded_red_rectangle));
        }

        private void updateViewsAsNotDone() {
            checkBox.setChecked(false);
            titleTextView.setPaintFlags(titleTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            titleTextView.setTextColor(Color.BLACK);
            deadlineTextView.setPaintFlags(deadlineTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            deadlineTextView.setTextColor(Color.BLACK);
            deadlineLabelTextView.setPaintFlags(deadlineTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            deadlineLabelTextView.setTextColor(Color.BLACK);
            statusTextView.setVisibility(View.INVISIBLE);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void updateViewsAsDone() {
            checkBox.setChecked(true);
            titleTextView.setPaintFlags(titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            titleTextView.setTextColor(Color.GRAY);
            deadlineTextView.setPaintFlags(deadlineTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            deadlineTextView.setTextColor(Color.GRAY);
            deadlineLabelTextView.setPaintFlags(deadlineTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            deadlineLabelTextView.setTextColor(Color.GRAY);
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setText(R.string.done);
            statusTextView.setBackground(context.getDrawable(R.drawable.rounded_green_rectangle));
        }

        private void findViews(View itemView) {
            titleTextView = itemView.findViewById(R.id.task_item_title);
            deadlineTextView = itemView.findViewById(R.id.task_item_deadline);
            deadlineLabelTextView = itemView.findViewById(R.id.task_item_deadline_label);
            checkBox = itemView.findViewById(R.id.task_item_checkbox);
            statusTextView = itemView.findViewById(R.id.task_item_status);
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d("BaoAnh", v.toString());
            showLongClickMenu(context, v);
            return true;
        }

        public void startEditTaskActivity() {
            editTaskIntent = new Intent(context, EditTaskActivity.class);
            editTaskIntent.putExtra("position", getAdapterPosition());
            context.startActivity(editTaskIntent);
        }

        public void showLongClickMenu(Context context, View view) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.task_long_click_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.delete_task) askForDeleteTask(context);
                else if (itemId == R.id.edit_task) {
                    startEditTaskActivity();
                }
                return true;
            });
            popupMenu.show();
        }

        public void askForDeleteTask(Context context) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

            DialogInterface.OnClickListener onCancelListener = (dialog, which) -> {
                // Just close the dialog
            };

            DialogInterface.OnClickListener onConfirmListener = (dialog, which) -> {
                // Cancel notification & Delete the task
                DataManager dataManager = DataManager.getInstance();
                int position = getAdapterPosition();
                dataManager.getTasks().get(position).cancelNotification(TaskAdapter.this.context);
                dataManager.deleteTask(position);
                TaskAdapter.this.notifyItemRemoved(position);
                TaskAdapter.this.notifyItemRangeChanged(position, TaskAdapter.this.tasks.size());
            };

            dialogBuilder
                    .setTitle(R.string.delete_task_question)
                    .setMessage(R.string.are_you_sure_that_you_want_to_delete_this_task)
                    .setNegativeButton(R.string.cancel, onCancelListener)
                    .setPositiveButton(R.string.yes, onConfirmListener);

            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        }
    }
}
