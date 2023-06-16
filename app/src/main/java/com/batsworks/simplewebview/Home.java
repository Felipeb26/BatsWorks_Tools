package com.batsworks.simplewebview;

import android.animation.LayoutTransition;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.batsworks.simplewebview.config.style.ScrollHorizontal;

public class Home extends Fragment {


    private HorizontalScrollView firstScrollView, secondScrollView, thirdScrollView;
    private Handler firstHandler, secondHandler, thirdHandler;
    private ImageView logoImageView;
    private CardView aboutCard;
    private TextView textDetails;
    private LinearLayout linearAbout;

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
        setupOnBackPressed();
        return view;
    }

    private void initComponents(View view) {
        aboutCard = view.findViewById(R.id.card_about);
        linearAbout = view.findViewById(R.id.linear_about);
        textDetails = view.findViewById(R.id.details);
        logoImageView = view.findViewById(R.id.logo_view);
        firstScrollView = view.findViewById(R.id.card_my_work);
        secondScrollView = view.findViewById(R.id.card_point);
        thirdScrollView = view.findViewById(R.id.card_youtube);

        linearAbout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        firstHandler = new Handler();
        secondHandler = new Handler();
        thirdHandler = new Handler();
    }

    private void clickElements() {
        aboutCard.setOnClickListener(click -> {
            int v = textDetails.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
            TransitionManager.beginDelayedTransition(linearAbout,new AutoTransition());
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
        float brightnessFactor = 1.5f;
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

//    @Override
//    public void onStart() {
//        super.onStart();
////        firstHandler.post(firstScroll.runScroll);
////        firstHandler.post(runScroll(firstHandler, firstScrollView, 0, 0));
//        secondHandler.removeCallbacks(runScroll(secondHandler, secondScrollView, 0, 0));
//        thirdHandler.removeCallbacks(runScroll(thirdHandler, thirdScrollView, 0, 0));
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        firstHandler.post(runScroll(firstHandler, firstScrollView, 0, 0));
//        secondHandler.removeCallbacks(runScroll(secondHandler, secondScrollView, 0, 0));
//        thirdHandler.removeCallbacks(runScroll(thirdHandler, thirdScrollView, 0, 0));
//    }

    private Runnable runScroll(Handler handler, HorizontalScrollView scrollView, int speed, int delay) {

        return new Runnable() {
            @Override
            public void run() {
                int currentScrollX = scrollView.getScrollX();
                int maxScrollX = scrollView.getChildAt(0).getWidth() - scrollView.getWidth();
                int newScrollX = currentScrollX + speed > 0 ? speed : 10;

                if (newScrollX >= maxScrollX) {
                    newScrollX = 0;
                }
                scrollView.scrollTo(newScrollX, 0);
                handler.postDelayed(this, delay > 0 ? delay : 50);
            }
        };
    }

}