package com.batsworks.simplewebview.config.web;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;

public class MyWebViewSetting {

    private final WebSettings settings;

    public MyWebViewSetting(WebSettings settings) {
        this.settings = settings;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setting() {
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
    }

    public void view() {
//        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
    }
}
