package com.example.coordinaresponder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Start SOS Animation
        View pulseView = findViewById(R.id.pulse_view);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        pulseView.startAnimation(pulse);

        // SOS Button Click - Directs to Emergency Form
        findViewById(R.id.sos_button).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmergencyFormActivity.class);
            startActivity(intent);
        });

        // Bottom Nav Listeners
        findViewById(R.id.nav_report).setOnClickListener(v -> {
            startActivity(new Intent(this, ReportActivity.class));
        });

        findViewById(R.id.nav_account).setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
        });
    }
}