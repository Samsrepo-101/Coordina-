package com.example.coordinaresponder;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class ReportDetailActivity extends AppCompatActivity {

    private String reportId;
    private FirebaseFirestore db;
    private TextView tvTitle, tvId, tvStatusBadge, tvTypeCard, tvLocation, tvTime, tvReporter, tvDescription;
    private TextView timeReported, timeEnRoute, timeOnSite, timeResolved;
    private View lineReported, lineEnRoute, lineOnSite;
    private ImageView dotEnRoute, dotOnSite, dotResolved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        db = FirebaseFirestore.getInstance();
        reportId = getIntent().getStringExtra("report_id");

        if (reportId == null) {
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
        db.collection("reports").document(reportId)
                .addSnapshotListener((doc, error) -> {
                    if (error != null || doc == null || !doc.exists()) return;

                    String type = doc.getString("emergency_type");
                    String status = doc.getString("status");
                    String location = doc.getString("location");
                    String details = doc.getString("details");
                    
                    Date dateReported = doc.getDate("report_time");
                    Date dateEnRoute = doc.getDate("en_route_time");
                    Date dateOnSite = doc.getDate("on_site_time");
                    Date dateResolved = doc.getDate("resolved_time");

                    tvTitle.setText(type + " Emergency");
                    tvTypeCard.setText(type);
                    tvId.setText("ID: " + reportId.substring(0, 8));
                    tvLocation.setText(location);
                    tvDescription.setText(details);
                    
                    if (dateReported != null) {
                        tvTime.setText(DateUtils.formatDateTime(this, dateReported.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_ABBREV_ALL));
                        timeReported.setText(DateUtils.formatDateTime(this, dateReported.getTime(), DateUtils.FORMAT_SHOW_TIME));
                    }
                    if (dateEnRoute != null) timeEnRoute.setText(DateUtils.formatDateTime(this, dateEnRoute.getTime(), DateUtils.FORMAT_SHOW_TIME));
                    if (dateOnSite != null) timeOnSite.setText(DateUtils.formatDateTime(this, dateOnSite.getTime(), DateUtils.FORMAT_SHOW_TIME));
                    if (dateResolved != null) timeResolved.setText(DateUtils.formatDateTime(this, dateResolved.getTime(), DateUtils.FORMAT_SHOW_TIME));

                    updateTimelineUI(status);
                    loadReporterName(doc.getString("user_id"));
                });
    }

    private void updateTimelineUI(String status) {
        tvStatusBadge.setText(status != null ? status.substring(0, 1).toUpperCase() + status.substring(1) : "Pending");
        
        int activeColor = ContextCompat.getColor(this, R.color.red_main);
        int inactiveColor = 0xFFE0E0E0;
        
        // Reset
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
        db.collection("users").document(userId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) tvReporter.setText(doc.getString("fullName"));
                });
    }
}
