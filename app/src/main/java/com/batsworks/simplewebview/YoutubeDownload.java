package com.batsworks.simplewebview;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.batsworks.simplewebview.config.notification.Snack;
import com.batsworks.simplewebview.config.recycle.BatAdapter;
import com.batsworks.simplewebview.config.web.CallBack;
import com.batsworks.simplewebview.config.web.MyBrowserConfig;
import com.batsworks.simplewebview.config.web.MyWebViewSetting;
import com.batsworks.simplewebview.observable.Request;
import com.batsworks.simplewebview.services.FileDownloader;
import io.reactivex.rxjava3.core.Observable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.util.Objects.isNull;

@SuppressLint({"StaticFieldLeak", "ServiceCast", "MissingPermission", "CheckResult"})
public class YoutubeDownload extends AppCompatActivity {

    private final ExecutorService downloadService = Executors.newSingleThreadExecutor();
    private static final String TAG = "22";
    private static View view;
    private FileDownloader downloader;
    private EditText editText;
    private AppCompatButton btnDownload, btnPreview, btnSearch;
    private WebView webView;
    private ProgressBar progressBar, downloadProgress;
    private Observable<Object> observable;
    private TextView percentBar;
    private String videoLink;
    private String mimeType;
    private float contentLenght;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_download);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        view = findViewById(android.R.id.content);
        initComponents();
        btnClick();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initComponents() {
        editText = findViewById(R.id.input_url);
        btnPreview = findViewById(R.id.btn_preview);
        btnSearch = findViewById(R.id.btn_search);
        btnDownload = findViewById(R.id.btn_download);
        webView = findViewById(R.id.download_webview);
        progressBar = findViewById(R.id.load_webview);
        downloadProgress = findViewById(R.id.download_progress);
        percentBar = findViewById(R.id.percent_text);

        webView.setWebViewClient(new CallBack(progressBar));
        webView.setWebChromeClient(new MyBrowserConfig(YoutubeDownload.this.getWindow(), webView));
        final MyWebViewSetting setting = new MyWebViewSetting(webView.getSettings());
        setting.setting();
        webView.setClickable(false);
        backgroundActions();
    }

    private void btnClick() {
        btnSearch.setOnClickListener(click -> {
            progressBar.setVisibility(progressBar.getVisibility() == VISIBLE ? GONE : VISIBLE);
            if (!isNull(editText.getText())) {
                downloadService.execute(() -> {
                    String request = Request.makeRequest(editText.getText().toString());
                    runOnUiThread(() -> {
                        Intent intent = new Intent(this, DownLoadOption.class);
                        intent.putExtra("class", request);
                        Snack.bar(view, "Displaying webview");

                        startActivity(intent);
                    });
                });
            }
        });
        btnDownload.setOnClickListener(click -> {
            if (!isNull(videoLink)) {
                webView.loadUrl(videoLink);
                showWebView();
                executeTask();
            }
        });
        btnPreview.setOnClickListener(click -> {
            webView.loadUrl(videoLink);
            showWebView();
        });
    }

    private void executeTask() {
        String title = "d389f02b21f60630c52d";
        downloader = new FileDownloader(YoutubeDownload.this, downloadProgress, percentBar, title, contentLenght);
        String place = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/" + title.concat(mimeType);

        new Thread(() -> downloader.makeRequest(videoLink, place));
    }

    private void downloadVideo() {
        try {
            String titleLink = "d389f02b21f60630c52d";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoLink));
            request.allowScanningByMediaScanner();
            request.setTitle(titleLink)
                    .setDescription(titleLink.concat(" is dowloading...."))
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, titleLink + mimeType)
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

    private void showWebView() {
        webView.setVisibility(webView.getVisibility() == VISIBLE ? GONE : VISIBLE);
        downloadProgress.setVisibility(downloadProgress.getVisibility() == VISIBLE ? GONE : VISIBLE);
        progressBar.setVisibility(progressBar.getVisibility() == VISIBLE ? GONE : VISIBLE);
    }

    private void backgroundActions() {
        try {
            Intent intent = getIntent();
            String mime = intent.getStringExtra("mime");
            String url = intent.getStringExtra("url");
            String size = intent.getStringExtra("size");

            if (url == null || url.trim().equals("")) {
                btnPreview.setVisibility(GONE);
                btnDownload.setVisibility(GONE);
                return;
            }

            videoLink = url;
            mimeType = mime.contains(BatAdapter.MediaType.MP4.getMedia()) ? BatAdapter.MediaType.MP4.getMedia() : BatAdapter.MediaType.WEBM.getMedia();
            contentLenght = Float.parseFloat(size);
            mimeType = "." + mimeType;
        } catch (Exception e) {
            e.printStackTrace();
            Snack.bar(view, e.getMessage());
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager.hasPrimaryClip()) {
            ClipData.Item item = manager.getPrimaryClip().getItemAt(0);
            editText.setText(item.getText().toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}