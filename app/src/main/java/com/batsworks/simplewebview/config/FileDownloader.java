package com.batsworks.simplewebview.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.batsworks.simplewebview.services.PushNotification;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressLint("MissingPermission")
public class FileDownloader {

    private PushNotification pushNotification;
    //    private static final int BUFFER_SIZE = 4096;
    private static final int BUFFER_SIZE = 8192;
    private static final String TAG = "23";
    private final ProgressBar progressBar;
    private final TextView textView;
    private String lastModified;
    private final Context context;
    private final String title;
    long totalBytesRead;
    private NotificationManagerCompat notificationManagerCompat;
    private static int limitMessage = 0;

    public FileDownloader(Context context, ProgressBar progressBar, TextView percentBar, String title) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = percentBar;
        this.title = title;
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
            int contentLength = connection.getContentLength();
            BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
            FileOutputStream fos = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            NotificationCompat.Builder notificationBuilder = pushNotification.create(context, title, "Downloading....");

            while ((bytesRead = is.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                final int currentProgress = (int) ((((double) totalBytesRead) / ((double) contentLength)) * 100d);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        long progress = (totalBytesRead * 100) / (contentLength);

                        if ((limitMessage % 5) == 0) {
                            notificationManagerCompat = NotificationManagerCompat.from(context);
                            notificationManagerCompat.notify(2, notificationBuilder
                                    .setOngoing(true)
                                    .setContentText((contentLength / 1000000) + "/" + currentProgress + "\t\r" + progress + "%")
                                    .setProgress((contentLength / 1000000), (int) Math.min(progress, 100), false).build());
                        }
                        limitMessage++;
                        progressBar.setProgress((int) progress);
                        textView.setText(String.valueOf(Math.min(currentProgress, 100)));
                    }
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
        }
    }

}