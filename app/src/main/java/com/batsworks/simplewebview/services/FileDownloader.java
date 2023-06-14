package com.batsworks.simplewebview.services;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.batsworks.simplewebview.R;
import com.batsworks.simplewebview.brodcast.NotificationReceiver;
import com.batsworks.simplewebview.notification.PushNotification;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressLint("MissingPermission")
public class FileDownloader {

    private static final String ACTION_PAUSE = "actionpause";
    private static final String ACTION_PLAY = "actionplay";
    private static final int BUFFER_SIZE = 8192;
    private static final String TAG = "23";
    private static int limitMessage = 0;
    private static String urlLink;
    private PushNotification pushNotification;
    private final ProgressBar progressBar;
    private final TextView textView;
    private String lastModified;
    private final Context context;
    private final String title;
    float totalBytesRead;
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder notificationBuilder;

    public FileDownloader(Context context, ProgressBar progressBar, TextView percentBar, String title) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = percentBar;
        this.title = title;
    }

    public void makeRequest(String urlRequest, String savePath) {
        pushNotification = new PushNotification();
        URL url;
        urlLink = urlRequest;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlRequest);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("Connection", "keep-alive");
            if (lastModified != null) {
                connection.setRequestProperty("If-Range", lastModified);
            }
            lastModified = connection.getHeaderField("Last-Modified");

            int status_code = connection.getResponseCode();
            if (status_code == HttpURLConnection.HTTP_OK) {
                downloadFile(connection, savePath);
            } else {
                textView.setText("Error request code" + status_code);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage() + "\n");
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private void downloadFile(HttpURLConnection connection, String path) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("url", urlLink);
            map.put("title", title);

            float contentLength = connection.getContentLength();
            BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
            FileOutputStream fos = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            notificationBuilder = pushNotification.create(context, title, "Downloading....");
            notificationBuilder
                    .setOnlyAlertOnce(true)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2, 3)
                    )
                    .addAction(R.drawable.home_icon, "HOME", homePendIntent(map))
                    .addAction(R.drawable.pause_icon, "PAUSE", pausePendIntent())
                    .addAction(R.drawable.play_icon, "PLAY", playPendIntent());

            while ((bytesRead = is.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                final double content_total_lenght = Math.floor(contentLength) / 1000000;
                final int currentProgress = (int) ((((double) totalBytesRead) / ((double) contentLength)) * 100d);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    int progress = (int) (totalBytesRead / contentLength * 100);

                    if ((limitMessage % 2) == 0) {
                        notificationManagerCompat = NotificationManagerCompat.from(context);
                        notificationManagerCompat.notify(2, notificationBuilder
                                .setContentText(content_total_lenght + "/" + currentProgress + "\t\r" + progress + "%")
                                .setProgress((int) content_total_lenght, (int) Math.min(progress, 100), false)
                                .setOnlyAlertOnce(true)
                                .setOngoing(true)
                                .build());
                    }
                    limitMessage++;
                    progressBar.setProgress((int) progress);
                    textView.setText(Math.min(currentProgress, 100));
                });

                // Handler interruptions or cancellations here
            }
            fos.close();
            bos.close();
            is.close();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage() + "\n");
            e.printStackTrace();
        } finally {
            limitMessage = 0;
            notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(2, notificationBuilder
                    .setOngoing(true)
                    .setContentText("Download have finish")
                    .setProgress((int) 100, 100, false)
                    .build());
        }
    }

    private PendingIntent pausePendIntent() {
        Intent pauseIntent = new Intent(context, NotificationReceiver.class)
                .setAction(ACTION_PAUSE)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getBroadcast(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
    }

    private PendingIntent playPendIntent() {
        Intent playIntent = new Intent(context, NotificationReceiver.class)
                .setAction(ACTION_PLAY)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
    }

    private PendingIntent homePendIntent(Map<String, String> map) {
        Intent playIntent = new Intent(context, NotificationReceiver.class)
                .setAction(Objects.toString(map))
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

    }


}