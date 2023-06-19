package com.batsworks.simplewebview.fragments;

import android.animation.LayoutTransition;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.batsworks.simplewebview.R;
import com.batsworks.simplewebview.config.web.CallBack;
import com.batsworks.simplewebview.config.web.MyBrowserConfig;
import com.batsworks.simplewebview.config.web.MyWebViewSetting;

import java.util.Arrays;
import java.util.List;

public class Home extends Fragment {

    private ImageView logoImageView;
    private CardView aboutCard;
    private TextView textDetails;
    private LinearLayout linearAbout;
    private WebView linkedinCard, githubCard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initComponents(view);
        increaseBright();
        clickElements();
        configWebViewCard();
        setupOnBackPressed();
        return view;
    }

    private void initComponents(View view) {
        aboutCard = view.findViewById(R.id.card_about);
        linearAbout = view.findViewById(R.id.linear_about);
        textDetails = view.findViewById(R.id.details);
        logoImageView = view.findViewById(R.id.logo_view);

        githubCard = view.findViewById(R.id.github_card);
        linkedinCard = view.findViewById(R.id.linkedin_card);

        linearAbout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

    }

    private void configWebViewCard() {
        githubCard.loadUrl("https://github.com/Felipeb26");
        linkedinCard.loadUrl("https://www.linkedin.com/in/felipe-batista-9b0122189/");

        List<WebView> webViews = Arrays.asList(linkedinCard, githubCard);

        webViews.forEach(webView -> {
            webView.setWebViewClient(new CallBack());
            webView.setWebChromeClient(new MyBrowserConfig(requireActivity().getWindow(), webView));
            final MyWebViewSetting setting = new MyWebViewSetting(webView.getSettings());
            setting.view();
            setting.setting();
        });

    }

    private void clickElements() {
        aboutCard.setOnClickListener(click -> {
            int v = textDetails.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
            TransitionManager.beginDelayedTransition(linearAbout, new AutoTransition());
            textDetails.setVisibility(v);
        });
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

    private void increaseBright() {
        float brightnessFactor = 1.7f;
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[]{
                brightnessFactor, 0, 0, 0, 0, // Red
                0, brightnessFactor, 0, 0, 0, // Green
                0, 0, brightnessFactor, 0, 0, // Blue
                0, 0, 0, 1, 0  // Alpha
        });

        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        logoImageView.setColorFilter(colorFilter);
    }

}