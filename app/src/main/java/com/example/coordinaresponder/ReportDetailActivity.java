package com.example.coordinaresponder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.coordinaresponder.models.EmergencyReport;
import com.example.coordinaresponder.models.UserProfile;
import com.example.coordinaresponder.network.SupabaseClient;
import com.example.coordinaresponder.network.SupabaseService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportDetailActivity extends AppCompatActivity {

    private String reportId;
    private SupabaseService supabaseService;
    private TextView tvTitle, tvId, tvStatusBadge, tvTypeCard, tvLocation, tvTime, tvReporter, tvDescription;
    private TextView timeReported, timeEnRoute, timeOnSite, timeResolved;
    private View lineReported, lineEnRoute, lineOnSite;
    private ImageView dotEnRoute, dotOnSite, dotResolved;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        supabaseService = SupabaseClient.getService();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        token = prefs.getString("token", null);

        reportId = getIntent().getStringExtra("report_id");

        if (reportId == null || token == null) {
            finish();
            return;
        }

        initViews();
        loadReportDetails();

        findViewById(R.id.btn_back_detail).setOnClickListener(v -> finish());
        
        findViewById(R.id.nav_home_detail).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        
        findViewById(R.id.nav_report_detail).setOnClickListener(v -> finish());
        
        findViewById(R.id.nav_account_detail).setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        });
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_detail_title);
        tvId = findViewById(R.id.tv_detail_id);
        tvStatusBadge = findViewById(R.id.tv_detail_status_badge);
        tvTypeCard = findViewById(R.id.tv_detail_type_card);
        tvLocation = findViewById(R.id.tv_detail_location);
        tvTime = findViewById(R.id.tv_detail_time);
        tvReporter = findViewById(R.id.tv_detail_reporter);
        tvDescription = findViewById(R.id.tv_detail_description);

        timeReported = findViewById(R.id.time_reported);
        timeEnRoute = findViewById(R.id.time_en_route);
        timeOnSite = findViewById(R.id.time_on_site);
        timeResolved = findViewById(R.id.time_resolved);

        lineReported = findViewById(R.id.line_reported);
        lineEnRoute = findViewById(R.id.line_en_route);
        lineOnSite = findViewById(R.id.line_on_site);

        dotEnRoute = findViewById(R.id.dot_en_route);
        dotOnSite = findViewById(R.id.dot_on_site);
        dotResolved = findViewById(R.id.dot_resolved);
    }

    private void loadReportDetails() {
        // Query single incident by ID
        supabaseService.getIncidentsFiltered(Config.SUPABASE_ANON_KEY, token, null, "eq." + reportId, null)
                .enqueue(new Callback<List<EmergencyReport>>() {
                    @Override
                    public void onResponse(Call<List<EmergencyReport>> call, Response<List<EmergencyReport>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            EmergencyReport report = response.body().get(0);
                            displayReport(report);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<EmergencyReport>> call, Throwable t) {
                        Toast.makeText(ReportDetailActivity.this, "Error loading details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayReport(EmergencyReport report) {
        tvTitle.setText(report.getType() + " Emergency");
        tvTypeCard.setText(report.getType());
        tvId.setText("ID: " + report.getId().substring(0, 8));
        tvLocation.setText(report.getAddress());
        // Description isn't in the provided incidents schema for USER app directly but could be 'status' or metadata
        tvDescription.setText("Emergency reported at " + report.getAddress());
        
        if (report.getReportedAt() != null) {
            tvTime.setText(report.getReportedAt().replace("T", " ").substring(0, 19));
            timeReported.setText(report.getReportedAt().substring(11, 16));
        }

        updateTimelineUI(report.getStatus());
        loadReporterName(report.getReportedBy());
    }

    private void updateTimelineUI(String status) {
        tvStatusBadge.setText(status != null ? status.substring(0, 1).toUpperCase() + status.substring(1) : "Open");
        
        int activeColor = ContextCompat.getColor(this, R.color.red_main);
        int inactiveColor = 0xFFE0E0E0;
        
        dotEnRoute.setImageTintList(ColorStateList.valueOf(inactiveColor));
        dotOnSite.setImageTintList(ColorStateList.valueOf(inactiveColor));
        dotResolved.setImageTintList(ColorStateList.valueOf(inactiveColor));
        lineReported.setBackgroundColor(inactiveColor);
        lineEnRoute.setBackgroundColor(inactiveColor);
        lineOnSite.setBackgroundColor(inactiveColor);

        if ("en route".equalsIgnoreCase(status)) {
            dotEnRoute.setImageTintList(ColorStateList.valueOf(activeColor));
            lineReported.setBackgroundColor(activeColor);
        } else if ("on site".equalsIgnoreCase(status)) {
            dotEnRoute.setImageTintList(ColorStateList.valueOf(activeColor));
            lineReported.setBackgroundColor(activeColor);
            dotOnSite.setImageTintList(ColorStateList.valueOf(activeColor));
            lineEnRoute.setBackgroundColor(activeColor);
        } else if ("resolved".equalsIgnoreCase(status)) {
            dotEnRoute.setImageTintList(ColorStateList.valueOf(activeColor));
            lineReported.setBackgroundColor(activeColor);
            dotOnSite.setImageTintList(ColorStateList.valueOf(activeColor));
            lineEnRoute.setBackgroundColor(activeColor);
            dotResolved.setImageTintList(ColorStateList.valueOf(activeColor));
            lineOnSite.setBackgroundColor(activeColor);
        }
    }

    private void loadReporterName(String userId) {
        if (userId == null) return;
        supabaseService.getUserProfile(Config.SUPABASE_ANON_KEY, token, "eq." + userId)
                .enqueue(new Callback<List<UserProfile>>() {
                    @Override
                    public void onResponse(Call<List<UserProfile>> call, Response<List<UserProfile>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            tvReporter.setText(response.body().get(0).getFullName());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserProfile>> call, Throwable t) {}
                });
    }
}
