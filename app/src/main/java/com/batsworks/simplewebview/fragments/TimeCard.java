package com.batsworks.simplewebview.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.batsworks.simplewebview.R;
import com.batsworks.simplewebview.config.web.CallBack;
import com.batsworks.simplewebview.config.web.MyBrowserConfig;

public class TimeCard extends Fragment {

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_card, container, false);
        initComponents(view);
        swipeRefresh();
        setupOnBackPressed();
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initComponents(View view) {
        webView = view.findViewById(R.id.timecard_webview);
        progressBar = view.findViewById(R.id.timecard_progress);
        swipeRefreshLayout = view.findViewById(R.id.timecard_refresh);

        webView.loadUrl("https://felipeb26.github.io/timecard/");
        webView.setWebViewClient(new CallBack(progressBar));

        webView.setWebChromeClient(new MyBrowserConfig(requireActivity().getWindow(), webView));
        WebSettings settings = webView.getSettings();
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setJavaScriptEnabled(true);
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

}