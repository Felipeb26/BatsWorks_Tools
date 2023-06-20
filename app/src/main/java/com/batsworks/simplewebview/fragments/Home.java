package com.batsworks.simplewebview.fragments;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.batsworks.simplewebview.R;

public class Home extends Fragment {

    private ImageView logoImageView;
    private CardView aboutCard;
    private TextView textDetails;
    private LinearLayout linearAbout;
    private ImageView githubCard, linkedinCard, whatsappCard, gmailCard;

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
        whatsappCard = view.findViewById(R.id.whatsapp_card);
        gmailCard = view.findViewById(R.id.gmail_card);

        linearAbout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

    }

    private void configWebViewCard() {
        linkedinCard.setOnClickListener(click -> goTo("https://www.linkedin.com/in/felipe-batista-9b0122189/"));
        githubCard.setOnClickListener(click -> goTo("https://github.com/Felipeb26"));
        whatsappCard.setOnClickListener(click -> goTo("https://wa.me//5511971404157?text=Evite%20enviar%20mensagem%20com%20conteudo%20ofensivo"));
        gmailCard.setOnClickListener(click-> goTo("mailto:felipeb2silva@gmail.com"));
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

    private void goTo(String s) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(s)));
    }

}