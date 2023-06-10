package com.batsworks.simplewebview.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.*;
import android.webkit.*;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import com.batsworks.simplewebview.R;

public class BatsWorksAdmin extends Fragment {

    private WebView webView;

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

    @SuppressLint("SetJavaScriptEnabled")
    private void initComponents(View group) {
        webView = group.findViewById(R.id.webview);
        webView.loadUrl("https://batsworks-admin.onrender.com/");
        webView.setWebViewClient(new CallBack());

        webView.setWebChromeClient(new MyBrowserConfig(requireActivity().getWindow(),webView));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
    }

    private void setupOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isEnabled()) {
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    public static class CallBack extends WebViewClient {
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

}