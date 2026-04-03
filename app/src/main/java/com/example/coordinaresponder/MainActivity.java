package com.example.coordinaresponder;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // Initialize SOS button animations
        setupSOSAnimations();

        // SOS Button Click
        findViewById(R.id.sos_button).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmergencyFormActivity.class);
            startActivity(intent);
        });

        // Bottom Navigation
        findViewById(R.id.nav_report).setOnClickListener(v -> {
            startActivity(new Intent(this, ReportActivity.class));
        });

        findViewById(R.id.nav_account).setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
        });
    }

    private void setupSOSAnimations() {
        View sosButton = findViewById(R.id.sos_button);
        View pulse1 = findViewById(R.id.pulse_view_1);
        View pulse2 = findViewById(R.id.pulse_view_2);
        View pulse3 = findViewById(R.id.pulse_view_3);

        if (sosButton != null) {
            // 1. Subtle breathing for the button itself
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(sosButton, "scaleX", 1f, 1.05f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(sosButton, "scaleY", 1f, 1.05f, 1f);
            scaleX.setRepeatCount(ValueAnimator.INFINITE);
            scaleY.setRepeatCount(ValueAnimator.INFINITE);
            scaleX.setDuration(2000);
            scaleY.setDuration(2000);
            scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleX.start();
            scaleY.start();
        }

        // 2. Ripple effects in the background
        if (pulse1 != null) startRipple(pulse1, 0);
        if (pulse2 != null) startRipple(pulse2, 800);
        if (pulse3 != null) startRipple(pulse3, 1600);
    }

    private void startRipple(View view, long delay) {
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.setAlpha(0.6f);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 2.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 2.5f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0.6f, 0f);

        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        alpha.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY, alpha);
        set.setDuration(2400);
        set.setStartDelay(delay);
        set.setInterpolator(new LinearInterpolator());
        set.start();
    }
}