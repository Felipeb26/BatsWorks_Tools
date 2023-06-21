package com.batsworks.simplewebview.config.web;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.*;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;

import java.io.InputStream;

public class CallBack extends WebViewClient {

    private ProgressBar progressBar;
    private WebView webView;

    public CallBack(ProgressBar progressBar, WebView webView) {
        this.progressBar = progressBar;
        this.webView = webView;
    }

    public CallBack(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public CallBack(WebView webView) {
        this.webView = webView;
    }

    public CallBack() {

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().acceptCookie();
        CookieManager.getInstance().flush();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        if (webView != null)
            webView.setInitialScale(1);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        view.loadUrl(request.getUrl().toString());
        return true;
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = String.valueOf(request.getUrl());
        if (!url.contains("google") || !url.contains("facebook"))
            return super.shouldInterceptRequest(view, request);
        return null;
    }

}