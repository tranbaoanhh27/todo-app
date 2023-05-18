package com.basoft.todo;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;

public class TaskTodo implements Serializable {
    private String title;
    private Calendar deadline;
    private boolean isDone;
    private int notificationId;
    public static final int NOTIFICATION_UNSCHEDULED = -1;

    public TaskTodo(String title, Calendar deadline) {
        this.title = title;
        this.deadline = deadline;
        this.isDone = false;
        notificationId = NOTIFICATION_UNSCHEDULED;
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

    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(Calendar deadline) {
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
                    deadline.get(Calendar.DAY_OF_MONTH),
                    deadline.get(Calendar.MONTH) + 1,
                    deadline.get(Calendar.YEAR),
                    deadline.get(Calendar.HOUR_OF_DAY),
                    deadline.get(Calendar.MINUTE)
            );
        else
            return context.getString(R.string.no_deadline);
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public boolean hasDeadline() {
        return deadline != null;
    }

    public boolean missedDeadline() {
        if (deadline == null) return false;
        Calendar today = Calendar.getInstance();
        return today.compareTo(deadline) >= 0;
    }

    public boolean isUrgent() {
        if (deadline == null) return false;
        Calendar todayAdd24hours = Calendar.getInstance();
        todayAdd24hours.add(Calendar.HOUR, 24);
        return todayAdd24hours.compareTo(deadline) >= 0;
    }

    public boolean isNotDone() {
        return !isDone;
    }

    public static class TaskTodoComparator implements Comparator<TaskTodo> {
        @Override
        public int compare(TaskTodo leftTask, TaskTodo rightTask) {
            if (leftTask.deadline == null) return 1;
            if (rightTask.deadline == null) return -1;
            return leftTask.deadline.compareTo(rightTask.deadline);
        }
    }
}
