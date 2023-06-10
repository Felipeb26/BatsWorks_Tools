package com.batsworks.simplewebview.fragments;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import com.batsworks.simplewebview.R;
import com.batsworks.simplewebview.observable.IntObservable;

import java.util.Observer;

public class Youtube extends Fragment {

    private WebView webView;
    private IntObservable intObservable;

    public Youtube(IntObservable intObservable) {
        this.intObservable = intObservable;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        initComponents(view);
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
    private void initComponents(View group) {
        webView = group.findViewById(R.id.webview);
        webView.loadUrl("https://youtube.com/");
        webView.setWebViewClient(new CallBack());

        webView.setWebChromeClient(new MyBrowserConfig(requireActivity().getWindow(), webView));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
    }

    private void setupOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isEnabled()) {
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    public static class CallBack extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            CookieManager.getInstance().setAcceptCookie(true);
            CookieManager.getInstance().acceptCookie();
            CookieManager.getInstance().flush();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }

    private void startPIP(Boolean aBoolean) {
        if (aBoolean) {
            PictureInPictureParams pictureInPictureParams = new PictureInPictureParams.Builder().build();
            requireActivity().enterPictureInPictureMode(pictureInPictureParams);
        }
    }

    private final Observer observer = (o, arg) -> {
        if (arg instanceof Boolean)
            startPIP((Boolean) arg);
    };


}