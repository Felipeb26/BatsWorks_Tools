package com.batsworks.simplewebview.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.batsworks.simplewebview.config.Format;
import com.batsworks.simplewebview.notification.PushNotification;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

@SuppressLint("MissingPermission")
public class FileDownloader {

    private static final int BUFFER_SIZE = 8196;
    private static final String TAG = "23";
    private PushNotification pushNotification;
    private final ProgressBar progressBar;
    private final TextView textView;
    private final Context context;
    private final String title;
    private final float contentLenght;
    float totalBytesRead;
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder notificationBuilder;

    public FileDownloader(Context context, ProgressBar progressBar, TextView percentBar, String title, float contentLeght) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = percentBar;
        this.title = title;
        this.contentLenght = contentLeght;
    }


    public void makeRequest(String urlRequest, String savePath) {
        pushNotification = new PushNotification();
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlRequest);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("Connection", "keep-alive");

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
        try (BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
             BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(path)), BUFFER_SIZE)) {


            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            notificationBuilder = pushNotification.create(context, title, "Downloading....");
            int lenght = 2;

            while ((bytesRead = is.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                double content_total_lenght = Math.floor(contentLenght) / 1000000;
                int progress = (int) (totalBytesRead / contentLenght * 100);

                Handler handler = new Handler(Looper.getMainLooper());
                int finalLenght = lenght;
                handler.post(() -> {
                    if ((finalLenght % 2) == 0) {
                        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                        inboxStyle.addLine(String.format(Locale.ROOT, "%.2f/%.2f", content_total_lenght, Format.bytesToMB(totalBytesRead)));
                        inboxStyle.addLine(progress + "%");

                        notificationManagerCompat = NotificationManagerCompat.from(context);
                        notificationManagerCompat.notify(2, notificationBuilder
                                .setContentText(String.format(Locale.ROOT, "%.2f/%.2f", content_total_lenght, Format.bytesToMB(totalBytesRead)))
                                .setProgress((int) content_total_lenght, (int) Math.min(Format.bytesToMB(totalBytesRead), 100), false)
                                .setOnlyAlertOnce(true)
                                .setOngoing(true)
                                .setStyle(inboxStyle)
                                .build());
                    }
                });
                lenght++;
                progressBar.setProgress(progress);
                if (progress > 0)
                    textView.setText(String.valueOf(Math.min(progress, 100)));

                // Handler interruptions or cancellations here
            }

            progressBar.setProgress(100);
            bos.flush();
        } catch (Exception e) {
            Log.i("24", e.getMessage() + "\n");
            e.printStackTrace();
        } finally {
            connection.disconnect();
            notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(2, notificationBuilder
                    .setContentText("Download have finish")
                    .setProgress((int) 100, 100, false)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .build());
        }
    }

}