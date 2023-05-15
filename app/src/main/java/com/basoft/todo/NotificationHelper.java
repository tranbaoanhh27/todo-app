package com.basoft.todo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;
import java.util.Calendar;

public class NotificationHelper {

    public static void scheduleNotification(Context context, Notification notification, LocalDateTime dateTime) {
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (dateTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
//            calendar.set(Calendar.YEAR, dateTime.getYear());
//            calendar.set(Calendar.MONTH, dateTime.getMonthValue() - 1);
//            calendar.set(Calendar.DAY_OF_MONTH, dateTime.getDayOfMonth());
            calendar.set(Calendar.HOUR_OF_DAY, dateTime.getHour());
            calendar.set(Calendar.MINUTE, dateTime.getMinute());
            Log.d("WTF", calendar.toString());
            Log.d("WTF", "" + calendar.getTimeInMillis());
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        }
    }

    public static Notification createNotification(Context context, String contentTitle, String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationReceiver.NOTIFICATION_CHANNEL_ID);

        Bitmap bitmapAppIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        builder.setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmapAppIcon)
                .setAutoCancel(true)
                .setChannelId(NotificationReceiver.NOTIFICATION_CHANNEL_ID);

        return builder.build();
    }
}
