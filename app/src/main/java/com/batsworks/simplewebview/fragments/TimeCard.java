package com.batsworks.simplewebview.fragments;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
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
import com.batsworks.simplewebview.config.web.AdBlocker;
import com.batsworks.simplewebview.config.web.CallBack;
import com.batsworks.simplewebview.config.web.MyBrowserConfig;
import com.batsworks.simplewebview.config.web.MyWebViewSetting;

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
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        initComponents(view);
        swipeRefresh();
        setupOnBackPressed();
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initComponents(View view) {
        AdBlocker.init(getContext());
        webView = view.findViewById(R.id.timecard_webview);
        progressBar = view.findViewById(R.id.timecard_progress);
        swipeRefreshLayout = view.findViewById(R.id.timecard_refresh);

        webView.loadUrl("https://felipeb26.github.io/timecard/");
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

}