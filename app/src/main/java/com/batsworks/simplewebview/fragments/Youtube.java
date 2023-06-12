package com.batsworks.simplewebview.fragments;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.batsworks.simplewebview.R;
import com.batsworks.simplewebview.config.CallBack;
import com.batsworks.simplewebview.config.MyBrowserConfig;
import com.batsworks.simplewebview.config.MyWebViewSetting;
import com.batsworks.simplewebview.observable.IntObservable;

import java.util.Observer;

public class Youtube extends Fragment {

    private WebView webView;
    private final IntObservable intObservable;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public Youtube(IntObservable intObservable) {
        this.intObservable = intObservable;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);
        initComponents(view);
        swipeRefresh();
        setupOnBackPressed();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isDetached()) {
            intObservable.addObserver(observer);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initComponents(View view) {
        webView = view.findViewById(R.id.youtube_webview);
        progressBar = view.findViewById(R.id.youtube_progress);
        swipeRefreshLayout = view.findViewById(R.id.youtube_refresh);

        webView.loadUrl("https://youtube.com/");
        webView.setWebViewClient(new CallBack(progressBar));

        webView.setWebChromeClient(new MyBrowserConfig(requireActivity().getWindow(), webView));
        final MyWebViewSetting setting = new MyWebViewSetting(webView.getSettings());
        setting.setting();
    }

    private void swipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            webView.reload();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setupOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isEnabled()) {
                    if (webView != null && webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        setEnabled(false);
                        requireActivity().onBackPressed();
                    }
                }
            }
        });
    }

    private final Observer observer = (o, arg) -> {
        if (arg instanceof Boolean) {
            if ((Boolean) arg) {
                PictureInPictureParams pictureInPictureParams = new PictureInPictureParams.Builder().build();
                requireActivity().enterPictureInPictureMode(pictureInPictureParams);
            }
        }
    };

}