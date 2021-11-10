package com.india.engaze.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.india.engaze.R;

import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

public class ProgressLine {

    ImageView progressBar;

    Runnable action = () -> repeatAnimation();
    Context context;

    public ProgressLine(ImageView progressBar, Context context) {
        this.progressBar = progressBar;
        this.context = context;
        initializeAvd();
        hideLoading();
    }

    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        progressBar.removeCallbacks(action);
    }

    public void showLoading(){
        repeatAnimation();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void repeatAnimation() {
        avdProgress.start();
        progressBar.postDelayed(action, 1000); // Will repeat animation in every 1 second
    }

    AnimatedVectorDrawableCompat avdProgress;


    private void initializeAvd() {
        avdProgress = AnimatedVectorDrawableCompat.create(context, R.drawable.avd_line);
        progressBar.setBackground(avdProgress);
        repeatAnimation();
    }

}
