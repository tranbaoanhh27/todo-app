package com.basoft.todo;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_LOCKED_BOOT_COMPLETED) || action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            DataManager dataManager = DataManager.getInstance();
            if (!dataManager.loadedTasks()) dataManager.loadTasksFromLocalStorage(context);
            for (TaskTodo task : dataManager.getTasks()) {
                task.scheduleNotification(context);
            }
            rescheduledNotify(context);
        }
    }

    public void rescheduledNotify(Context context) {
        Calendar notificationTime = Calendar.getInstance();
        Notification notification = NotificationHelper.createNotification(
                context,
                context.getString(R.string.notifications_rescheduled),
                context.getString(R.string.the_notifications_for_the_tasks_have_just_been_rescheduled)
        );
        NotificationHelper.scheduleNotification(context, notification, notificationTime);
    }
}
