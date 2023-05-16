package com.basoft.todo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class NotificationHelper {

    public static int notificationId = 1;

    public static void scheduleNotification(Context context, Notification notification, Calendar dateTime) {
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, notificationId, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        notificationId++;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (dateTime != null)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dateTime.getTimeInMillis(), pendingIntent);
    }

    public static Notification createNotification(Context context, String contentTitle, String contentText) {
        Intent onNotificationClickIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(onNotificationClickIntent);
        PendingIntent onNotificationClickPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationReceiver.NOTIFICATION_CHANNEL_ID);
        builder.setContentIntent(onNotificationClickPendingIntent);

        Bitmap bitmapAppIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground);
        builder.setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(bitmapAppIcon)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setAutoCancel(true)
                .setChannelId(NotificationReceiver.NOTIFICATION_CHANNEL_ID);

        return builder.build();
    }
}
