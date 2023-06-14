package com.batsworks.simplewebview;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import com.batsworks.simplewebview.config.CallBack;
import com.batsworks.simplewebview.notification.Snack;
import com.batsworks.simplewebview.observable.Request;
import com.batsworks.simplewebview.services.FileDownloader;
import io.reactivex.rxjava3.core.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.util.Objects.isNull;

@SuppressLint({"StaticFieldLeak", "ServiceCast", "MissingPermission"})
public class YoutubeDownload extends AppCompatActivity {
    private static final String ACTION_PAUSE = "actionpause";
    private static final String ACTION_PLAY = "actionplay";
    private static final int tag = 22;
    private static View view;
    private FileDownloader downloader;
    private EditText editText;
    private AppCompatButton btnDownload, btnPreview;
    private WebView webView;
    private ProgressBar progressBar, downloadProgress;
    private TextView percentBar;
    private String videoLink;
    private String titleLink;
    private final ExecutorService downloadService = Executors.newFixedThreadPool(3);
    private Observable<Map<String, String>> observable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_download);
        initComponents();
        btnClick();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        view = findViewById(android.R.id.content);
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
        backgroundActions();
    }

    private void btnClick() {
        btnDownload.setOnClickListener(click -> {
            if (!isNull(editText.getText())) {
                Request request = new Request();
                Object s = request.executeOnExecutor(downloadService, editText.getText().toString());
                System.out.println("vkjfv");
            }
        });
        btnPreview.setOnClickListener(click -> {
            if (!isNull(editText.getText())) {
                if (videoLink == null) {
                    extractVideo(editText.getText().toString(), 0);
                } else {
                    webView.loadUrl(videoLink);
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void extractVideo(String videoUrl, int i) {
        final ExecutorService service = Executors.newSingleThreadExecutor();
        showWebView(i);
        try {
            new YouTubeExtractor(this) {

                @Override
                protected void onCancelled(SparseArray<YtFile> ytFileSparseArray) {
                    super.onCancelled(ytFileSparseArray);
                    System.out.println(ytFileSparseArray.get(0));
                }

                @Override
                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                    Log.i("5", String.valueOf(values.length));
                }

                @Override
                protected void onExtractionComplete(SparseArray<YtFile> files, VideoMeta videoMeta) {
                    if (files != null) {
                        try {
                            Map<String, String> map = new HashMap<>();

                            videoLink = files.get(tag).getUrl();

                            observable = Observable.create(emitter -> {
                                map.put("url", files.get(tag).getUrl());
                                emitter.onNext(map);
                            });

                            observable.subscribe(make -> {
                                        progressBar.setVisibility(GONE);
                                        if (i == 1)
                                            executeTask();
                                        if (i == 0)
                                            webView.loadUrl(videoLink);
                                    }, throwable -> Snack.errorBar(view, throwable.getMessage()),
                                    () -> {
                                        if (i == 1)
                                            Snack.bar(view, "Download start");
                                        if (i == 0) {
                                            Snack.bar(view, "Displaying webview");
                                        }
                                    });
                        } catch (Exception e) {
                            Snack.errorBar(view, e.getMessage());
                        }
                    }
                }
            }.executeOnExecutor(service, videoUrl);
        } catch (Exception e) {
            Snack.errorBar(view, e.getMessage());
        }
    }

    private void executeTask() {
        downloader = new FileDownloader(YoutubeDownload.this, downloadProgress, percentBar, titleLink);
        String place = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/" + titleLink.concat(".mp4");
        Handler handler = new Handler(Looper.getMainLooper());

        downloadService.execute(new Runnable() {
            @Override
            public void run() {
                downloader.makeRequest(videoLink, place);
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
            if (videoLink == null) {
                extractVideo(editText.getText().toString(), 1);
                return;
            }
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoLink));
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
            Snack.bar(view, e.getMessage());
        }
    }

    private void showWebView(int i) {
        if (i == 0)
            webView.setVisibility(webView.getVisibility() == VISIBLE ? GONE : VISIBLE);
        downloadProgress.setIndeterminate(false);
        progressBar.setVisibility(progressBar.getVisibility() == VISIBLE ? GONE : VISIBLE);
    }

    private void backgroundActions() {
        try {
            Intent intent = getIntent();
            String action = intent.getStringExtra("ActionName");
            if (action == null)
                return;
            if (action.equals(ACTION_PAUSE)) {
//                stopThread();
                if (downloadService != null) {
                    Log.i("51", downloadService.isTerminated() + "\n");
                    showWebView(1);
                    downloadService.shutdownNow();
                    Snack.bar(view, "pausado com sucesso");
                }
                return;
            }

            if (action.equals(ACTION_PLAY)) {
                if (downloadService == null) {
                    extractVideo(videoLink, 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Snack.bar(view, e.getMessage());
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager.hasPrimaryClip()) {
            ClipData.Item item = manager.getPrimaryClip().getItemAt(0);
            editText.setText(item.getText().toString());
        }
        super.onWindowFocusChanged(hasFocus);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}