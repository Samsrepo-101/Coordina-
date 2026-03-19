package com.example.coordinaresponder;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class EmergencyFormActivity extends AppCompatActivity implements SelectTypeFragment.OnTypeSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emergency_form);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_nav), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            SelectTypeFragment fragment = new SelectTypeFragment();
            fragment.setOnTypeSelectedListener(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }

        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        
        findViewById(R.id.nav_report).setOnClickListener(v -> {
            startActivity(new Intent(this, ReportActivity.class));
            finish();
        });
        
        findViewById(R.id.nav_account).setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        });
    }

    @Override
    public void onTypeSelected(String type) {
        FillDetailsFragment detailsFragment = FillDetailsFragment.newInstance(type);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit();
    }
}