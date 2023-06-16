package com.batsworks.simplewebview;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.batsworks.simplewebview.config.style.ScrollHorizontal;

public class Home extends Fragment {


    private HorizontalScrollView firstScrollView, secondScrollView, thirdScrollView;
    private Handler firstHandler, secondHandler, thirdHandler;
    private ScrollHorizontal firstScroll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initComponents(view);
        setupOnBackPressed();
        return view;
    }

    private void initComponents(View view) {
        firstScrollView = view.findViewById(R.id.card_my_work);
        secondScrollView = view.findViewById(R.id.card_point);
        thirdScrollView = view.findViewById(R.id.card_youtube);
        firstHandler = new Handler();
        secondHandler = new Handler();
        thirdHandler = new Handler();

        firstScroll = new ScrollHorizontal(firstHandler, firstScrollView, 0, 0);
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

    @Override
    public void onStart() {
        super.onStart();
        firstHandler.post(runScroll);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private final Runnable runScroll = new Runnable() {
        @Override
        public void run() {
            int currentScrollX = firstScrollView.getScrollX();
            int maxScrollX = firstScrollView.getChildAt(0).getWidth() - firstScrollView.getWidth();
            int newScrollX = 0;

            if (newScrollX < maxScrollX) {
                newScrollX = currentScrollX + 10;
            }

            while (newScrollX >= maxScrollX) {
                newScrollX = currentScrollX - 10;

                firstScrollView.scrollTo(newScrollX, 0);
                firstHandler.postDelayed(this, 50);
            }
            firstScrollView.scrollTo(newScrollX, 0);
            firstHandler.postDelayed(this, 50);
        }
    };

}