package com.batsworks.simplewebview.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.batsworks.simplewebview.config.notification.PushNotification;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressLint("MissingPermission")
public class FileDownloader {

    private static final int BUFFER_SIZE = 8192;
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

    private Void downloadFile(HttpURLConnection connection, String path) {
        try {
            float contentLength = connection.getContentLength();
            BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
            FileOutputStream fos = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            notificationBuilder = pushNotification.create(context, title, "Downloading....");
            notificationBuilder
                    .setOnlyAlertOnce(true)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle());

            while ((bytesRead = is.read(buffer)) != -1) {

                float contentSize = contentLenght >= 0 ? contentLength : contentLenght;
                final double content_total_lenght = Math.floor(contentSize) / 1000000;
                final int currentProgress = (int) ((((double) totalBytesRead) / ((double) contentSize)) * 100d);

//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(() -> {
                int progress = (int) (totalBytesRead / contentSize * 100);
                if ((content_total_lenght % 2) == 0) {
                    notificationManagerCompat = NotificationManagerCompat.from(context);
                    notificationManagerCompat.notify(2, notificationBuilder
                            .setContentText(content_total_lenght + "/" + currentProgress + "\t\r" + progress + "%")
                            .setProgress((int) content_total_lenght, (int) Math.min(progress, 100), false)
                            .setOnlyAlertOnce(true)
                            .setOngoing(true)
                            .build());
                }
                progressBar.setProgress(progress);
                if (progress > 0)
                    textView.setText(Math.min(currentProgress, 100));
//                });
                bos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                // Handler interruptions or cancellations here
            }

            progressBar.setProgress(100);
            is.close();
            fos.flush();
            fos.close();
            bos.flush();
            bos.close();
            connection.disconnect();
        } catch (Exception e) {
            Log.i("24", e.getMessage() + "\n");
            e.printStackTrace();
        } finally {
            notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(2, notificationBuilder
                    .setOngoing(true)
                    .setContentText("Download have finish")
                    .setProgress((int) 100, 100, false)
                    .build());
        }
        return null;
    }

}