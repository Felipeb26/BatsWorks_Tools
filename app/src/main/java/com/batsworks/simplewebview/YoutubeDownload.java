package com.batsworks.simplewebview;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.*;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.batsworks.simplewebview.config.recycle.BatAdapter;
import com.batsworks.simplewebview.config.web.CallBack;
import com.batsworks.simplewebview.config.web.MyBrowserConfig;
import com.batsworks.simplewebview.config.web.MyWebViewSetting;
import com.batsworks.simplewebview.notification.BatsAlertDialog;
import com.batsworks.simplewebview.notification.Snack;
import com.batsworks.simplewebview.observable.Request;
import com.batsworks.simplewebview.services.FileDownloader;

import java.io.File;
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
    private EditText editText, textFileName;
    private AppCompatButton btnDownload, btnPreview, btnSearch, btnClean;
    private WebView webView;
    private ProgressBar progressBar, downloadProgress;
    private TextView percentBar;
    private String videoLink;
    private String mimeType;
    private float contentLenght;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_download);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        btnClean = findViewById(R.id.btn_clean);
        textFileName = findViewById(R.id.text_file_name);

        webView.setWebViewClient(new CallBack(progressBar));
        webView.setWebChromeClient(new MyBrowserConfig(YoutubeDownload.this.getWindow(), webView));
        final MyWebViewSetting setting = new MyWebViewSetting(webView.getSettings());
        setting.setting();
        webView.setClickable(false);
        backgroundActions();
    }

    private void btnClick() {
        btnSearch.setOnClickListener(click -> {
            if (isNull(editText.getText()) || !editText.getText().toString().contains("youtu")) {
                Snack.errorBar(view, "Url precisa pertencer ao site https://youtube.com");
                return;
            }
            progressBar.setVisibility(progressBar.getVisibility() == VISIBLE ? GONE : VISIBLE);
            downloadService.execute(() -> {
                String request = Request.makeRequest(editText.getText().toString());
                runOnUiThread(() -> {
                    BatsAlertDialog.alert(this, "Atencao",
                            "necessario selecionar um item para o download,\napos iniciar o download não feche o app.",
                            request, this::showDowloandList);
                });
            });
        });
        btnDownload.setOnClickListener(click -> {
            if (isNull(videoLink)) {
                Snack.errorBar(view, "Url não localizada para download");
                return;
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                btnDownload.setEnabled(false);
            });
            webView.loadUrl(videoLink);
            showWebView();
            executeTask();
        });
        btnPreview.setOnClickListener(click -> {
            if (isNull(videoLink)) {
                Snack.errorBar(view, "Url não localizada para download");
                return;
            }
            webView.loadUrl(videoLink);
            showWebView();
        });
        btnClean.setOnClickListener(click -> {
            contentLenght = 0;
            videoLink = null;
            mimeType = null;
            webView.loadUrl("");
            editText.setText("");
            btnPreview.setVisibility(GONE);
            btnDownload.setVisibility(GONE);
            btnClean.setVisibility(GONE);
        });
    }

    private Void showDowloandList(String request) {
        Intent intent = new Intent(this, DownLoadOption.class);
        intent.putExtra("class", request);
        startActivity(intent);
        return null;
    }

    private void executeTask() {
        String path = mimeType.contains(BatAdapter.MediaType.MP4.getMedia()) ? "/videos/" : "/musics/";
        ExecutorService service = Executors.newFixedThreadPool(2);
        String title = textFileName.getText() == null ? "batsworks_download" : textFileName.getText().toString();
        title = title.trim().equals("") ? "batsworks_download" : title;
        findFolder(path);
        FileDownloader downloader = new FileDownloader(YoutubeDownload.this, downloadProgress, percentBar, title, contentLenght);
        String place = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + path + title.concat(mimeType);
        service.execute(() -> downloader.makeRequest(videoLink, place));
    }

    private void findFolder(String path) {
        final String finalPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + path;
        File folder = new File(finalPath);
        if (!folder.exists()) {
            folder.mkdirs();
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
                btnClean.setVisibility(GONE);
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
        startActivity(new Intent(this, MainActivity.class));
    }

}