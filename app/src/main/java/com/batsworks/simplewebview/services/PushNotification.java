package com.batsworks.simplewebview.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.batsworks.simplewebview.R;
import com.batsworks.simplewebview.YoutubeDownload;

public class PushNotification {

    private static String ID = "26";
    private NotificationManagerCompat notificationManagerCompat;
    private Notification notification;


    @SuppressLint("MissingPermission")
    public void create(Context context, String title, String message, int max, int now) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID, "follow_download",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID)
                .setSmallIcon(R.drawable.batsworks)
                .setVibrate(new long[]{0})
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendIntent(context))
                .setProgress(max, now, false);
        notification = builder.build();
        notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(2, notification);
    }

    @SuppressLint("MissingPermission")
    public NotificationCompat.Builder create(Context context, String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID, "follow_download",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(context, ID)
                .setSmallIcon(R.drawable.batsworks)
                .setVibrate(new long[]{0})
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendIntent(context));
    }

    private PendingIntent pendIntent(Context context) {
        Intent intent = new Intent(context, YoutubeDownload.class);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);
    }

}
