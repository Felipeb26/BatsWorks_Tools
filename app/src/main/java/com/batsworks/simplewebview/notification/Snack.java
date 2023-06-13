package com.batsworks.simplewebview.notification;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import com.batsworks.simplewebview.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class Snack {

    @SuppressLint("ResourceAsColor")
    public static void bar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
        snackbar.setBackgroundTintMode(PorterDuff.Mode.DARKEN);
        snackbar.setTextColor(ContextCompat.getColor(view.getContext(), android.R.color.white));

        snackbar.setAction("X", viewClick -> snackbar.dismiss());
        snackbar.setActionTextColor(Color.WHITE);

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.cyan_mid));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarView.setLayoutParams(params);
        snackbar.show();
    }
    public static void errorBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
        snackbar.setBackgroundTint(Color.RED);
        snackbar.setTextColor(ContextCompat.getColor(view.getContext(), android.R.color.white));

        snackbar.setAction("X", viewClick -> snackbar.dismiss());
        snackbar.setActionTextColor(Color.WHITE);

        View snackbarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarView.setLayoutParams(params);
        snackbar.show();
    }

}
