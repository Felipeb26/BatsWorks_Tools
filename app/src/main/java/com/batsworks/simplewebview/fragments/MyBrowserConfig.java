package com.batsworks.simplewebview.fragments;

import android.os.Build;
import android.view.*;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MyBrowserConfig extends WebChromeClient {

    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private final Window window;
    private final WebView webView;

    public MyBrowserConfig(Window window, WebView webView) {
        this.window = window;
        this.webView = webView;
    }


    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        if (mCustomView != null) {
            onHideCustomView();
            return;
        }
        mCustomView = view;
        mCustomViewCallback = callback;

        ViewGroup decor = (ViewGroup) window.getDecorView();
        decor.addView(mCustomView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mCustomViewCallback.onCustomViewHidden();
    }

    @Override
    public void onHideCustomView() {
        if (mCustomView == null) {
            return;
        }
        mCustomViewCallback.onCustomViewHidden();

        ViewGroup decor = (ViewGroup) window.getDecorView();
        decor.removeView(mCustomView);
        mCustomView = null;
    }
}
