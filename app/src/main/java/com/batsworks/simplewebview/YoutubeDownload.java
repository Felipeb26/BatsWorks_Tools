package com.batsworks.simplewebview;

import static java.util.Objects.isNull;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.SparseArray;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.batsworks.simplewebview.config.CallBack;
import com.batsworks.simplewebview.config.FileDownloader;
import com.batsworks.simplewebview.services.PushNotification;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

@SuppressLint({"StaticFieldLeak", "ServiceCast"})
public class YoutubeDownload extends AppCompatActivity {

    private PushNotification pushNotification;
    private FileDownloader downloader;
    private EditText editText;
    private AppCompatButton btnDownload, btnPreview;
    private WebView webView;
    private static final int tag = 22;
    private ProgressBar progressBar, downloadProgress;
    private TextView percentBar;
    private String newLink;
    private String idLink;
    private String titleLink;
    private ExecutorService downloadService;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_download);
        initComponents();
        btnClick();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        pushNotification = new PushNotification();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initComponents() {
        editText = findViewById(R.id.input_url);
        btnPreview = findViewById(R.id.btn_preview);
        btnDownload = findViewById(R.id.btn_download);
        webView = findViewById(R.id.download_webview);
        progressBar = findViewById(R.id.load_webview);
        downloadProgress = findViewById(R.id.download_progress);
        percentBar = findViewById(R.id.percent_text);

        webView.setWebViewClient(new CallBack(progressBar));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
    }

    private void btnClick() {
        btnDownload.setOnClickListener(click -> {
            if (!isNull(editText.getText())) {
                if (idLink == null) {
                    extractVideo(editText.getText().toString());
                }
                executeTask();
            }
        });
        btnPreview.setOnClickListener(click -> {
            if (!isNull(editText.getText())) {
                if (idLink == null)
                    extractVideo(editText.getText().toString());
                if (idLink != null)
                    webView.loadUrl(String.format("https://youtube.com/embed/%s", idLink));
            }
        });
    }

    private void extractVideo(String videoLink) {
        YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, String title, SparseArray<YtFile> files) {
                if (files != null) {
                    try {
                        newLink = files.get(tag).getUrl();
                        idLink = videoId;
                        titleLink = title;
                    } catch (Exception e) {
                        Toast.makeText(YoutubeDownload.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        youTubeUriExtractor.executeOnExecutor(service, videoLink);
    }

    private void executeTask() {
        if (newLink == null)
            return;
        downloader = new FileDownloader(YoutubeDownload.this, downloadProgress, percentBar, titleLink);
        String place = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/" + titleLink.concat(".mp4");
        downloadService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        downloadService.execute(new Runnable() {
            @Override
            public void run() {
                downloader.makeRequest(newLink, place);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });
    }

    private void downloadVideo() {
        try {
            if (newLink == null) {
                extractVideo(editText.getText().toString());
                return;
            }
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(newLink));
            request.allowScanningByMediaScanner();
            request.setTitle(titleLink)
                    .setDescription(titleLink.concat(" is dowloading...."))
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, titleLink + ".mp4")
                    .setVisibleInDownloadsUi(true)
                    .setAllowedOverRoaming(true)
                    .setAllowedOverMetered(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } catch (Exception e) {
            Toast.makeText(YoutubeDownload.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}