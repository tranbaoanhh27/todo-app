package com.basoft.todo;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;

public class TaskTodo implements Serializable {
    private String title;
    private LocalDateTime deadline;
    private boolean isDone;

    public TaskTodo(String title, LocalDateTime deadline) {
        this.title = title;
        this.deadline = deadline;
        this.isDone = false;
    }

    public TaskTodo(String title) {
        this(title, null);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @NonNull
    @Override
    public String toString() {
        if (deadline != null)
            return String.format("%s %s %b", title, deadline, isDone);
        else
            return String.format("%s null %b", title, isDone);
    }

    @SuppressLint("DefaultLocale")
    public String getDeadlineString(Context context) {
        if (deadline != null)
            return String.format(
                    "%02d/%02d/%04d %02d:%02d",
                    deadline.getDayOfMonth(),
                    deadline.getMonthValue(),
                    deadline.getYear(),
                    deadline.getHour(),
                    deadline.getMinute()
            );
        else
            return context.getString(R.string.no_deadline);
    }

    public static class TaskTodoComparator implements Comparator<TaskTodo> {
        @Override
        public int compare(TaskTodo leftTask, TaskTodo rightTask) {
            if (leftTask.isDone || leftTask.deadline == null) return 1;
            if (rightTask.isDone || rightTask.deadline == null) return -1;
            return leftTask.deadline.compareTo(rightTask.deadline);
        }
    }
}
