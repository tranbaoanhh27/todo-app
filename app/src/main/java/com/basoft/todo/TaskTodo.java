package com.basoft.todo;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;

public class TaskTodo implements Serializable, Cloneable {
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

    public TaskTodo setTitle(String title) {
        this.title = title;
        return this;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public TaskTodo setDeadline(Calendar deadline) {
        this.deadline = deadline;
        return this;
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
        if (deadline != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy HH:mm", Locale.getDefault());
            return formatter.format(deadline.getTime());
        }
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

    public void scheduleNotification(Context context) {
        if (hasDeadline() && !missedDeadline()) {
            Calendar notificationTime = (Calendar) deadline.clone();
            notificationTime.add(Calendar.MINUTE, -10);
            Notification notification = NotificationHelper.createNotification(
                    context,
                    String.format("%s - %s", title, context.getString(R.string._10_minutes_remaining)),
                    String.format(
                            "%s %s - %s",
                            context.getString(R.string.it_is_only_10_minutes_left_until_your_deadline_on),
                            title,
                            getDeadlineString(context)
                    )
            );
            int notificationId = NotificationHelper.scheduleNotification(context, notification, notificationTime);
            setNotificationId(notificationId);
        }
    }

    public TaskTodo cancelNotification(Context context) {
        if (notificationId != NOTIFICATION_UNSCHEDULED) {
            NotificationHelper.cancelNotification(context, notificationId);
            notificationId = NOTIFICATION_UNSCHEDULED;
        }
        return this;
    }

    @NonNull
    @Override
    public TaskTodo clone() {
        try {
            TaskTodo clone = (TaskTodo) super.clone();
            clone.deadline = deadline != null ? (Calendar) deadline.clone() : null;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
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
