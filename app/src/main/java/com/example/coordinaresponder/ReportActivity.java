package com.example.coordinaresponder;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coordinaresponder.models.EmergencyReport;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private RecyclerView rvReports;
    private ReportAdapter adapter;
    private List<EmergencyReport> reportList;
    private List<String> reportIds;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListenerRegistration reportListener;
    
    private TextView filterAll, filterActive, filterResolved;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvReports = findViewById(R.id.rv_reports);
        rvReports.setLayoutManager(new LinearLayoutManager(this));
        
        reportList = new ArrayList<>();
        reportIds = new ArrayList<>();
        adapter = new ReportAdapter(reportList, id -> {
            Intent intent = new Intent(ReportActivity.this, ReportDetailActivity.class);
            intent.putExtra("report_id", id);
            startActivity(intent);
        });
        rvReports.setAdapter(adapter);

        filterAll = findViewById(R.id.filter_all);
        filterActive = findViewById(R.id.filter_active);
        filterResolved = findViewById(R.id.filter_resolved);

        filterAll.setOnClickListener(v -> updateFilter("all"));
        filterActive.setOnClickListener(v -> updateFilter("active"));
        filterResolved.setOnClickListener(v -> updateFilter("resolved"));

        loadUserReports("all");

        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.nav_account).setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        });
    }

    private void updateFilter(String filter) {
        currentFilter = filter;

        int selectedBg = ContextCompat.getColor(this, R.color.light_red);
        int selectedText = ContextCompat.getColor(this, R.color.red_main);
        int unselectedBg = ContextCompat.getColor(this, android.R.color.white);
        int unselectedText = ContextCompat.getColor(this, R.color.grey_text);

        filterAll.setBackgroundTintList(ColorStateList.valueOf(filter.equals("all") ? selectedBg : unselectedBg));
        filterAll.setTextColor(filter.equals("all") ? selectedText : unselectedText);
        
        filterActive.setBackgroundTintList(ColorStateList.valueOf(filter.equals("active") ? selectedBg : unselectedBg));
        filterActive.setTextColor(filter.equals("active") ? selectedText : unselectedText);

        filterResolved.setBackgroundTintList(ColorStateList.valueOf(filter.equals("resolved") ? selectedBg : unselectedBg));
        filterResolved.setTextColor(filter.equals("resolved") ? selectedText : unselectedText);

        loadUserReports(filter);
    }

    private void loadUserReports(String filter) {
        if (mAuth.getCurrentUser() == null) return;
        if (reportListener != null) reportListener.remove();

        String userId = mAuth.getCurrentUser().getUid();
        Query query = db.collection("reports").whereEqualTo("user_id", userId);

        if ("active".equals(filter)) {
            query = query.whereIn("status", List.of("pending", "en route", "on site"));
        } else if ("resolved".equals(filter)) {
            query = query.whereEqualTo("status", "resolved");
        }

        query = query.orderBy("report_time", Query.Direction.DESCENDING);

        reportListener = query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (value != null) {
                reportList.clear();
                reportIds.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    reportList.add(doc.toObject(EmergencyReport.class));
                    reportIds.add(doc.getId());
                }
                adapter.setIds(reportIds);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reportListener != null) reportListener.remove();
    }

    private static class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
        private final List<EmergencyReport> reports;
        private List<String> ids;
        private final OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(String reportId);
        }

        public ReportAdapter(List<EmergencyReport> reports, OnItemClickListener listener) {
            this.reports = reports;
            this.listener = listener;
        }

        public void setIds(List<String> ids) {
            this.ids = ids;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            EmergencyReport report = reports.get(position);
            String reportId = ids.get(position);

            holder.tvType.setText(report.getEmergencyType() + " Emergency");
            holder.tvLocation.setText(report.getLocation());
            
            String status = report.getStatus();
            if (status == null) status = "pending";

            int activeColor = 0xFFF44336; // red_main
            int inactiveColor = 0xFFE0E0E0;
            int greyText = 0xFF757575;

            // Reset labels
            holder.tvLabelEnRoute.setTextColor(inactiveColor);
            holder.tvLabelOnSite.setTextColor(inactiveColor);
            holder.tvLabelResolved.setTextColor(inactiveColor);

            // Reset dots and lines
            holder.ivStepEnRoute.setImageTintList(ColorStateList.valueOf(inactiveColor));
            holder.vLine1.setBackgroundColor(inactiveColor);
            holder.ivStepOnSite.setImageTintList(ColorStateList.valueOf(inactiveColor));
            holder.vLine2.setBackgroundColor(inactiveColor);
            holder.ivStepResolved.setImageTintList(ColorStateList.valueOf(inactiveColor));
            holder.vLine3.setBackgroundColor(inactiveColor);

            if ("en route".equalsIgnoreCase(status)) {
                holder.tvLabelEnRoute.setTextColor(activeColor);
                holder.ivStepEnRoute.setImageTintList(ColorStateList.valueOf(activeColor));
                holder.vLine1.setBackgroundColor(activeColor);
            } else if ("on site".equalsIgnoreCase(status)) {
                holder.tvLabelEnRoute.setTextColor(greyText);
                holder.tvLabelOnSite.setTextColor(activeColor);
                holder.ivStepEnRoute.setImageTintList(ColorStateList.valueOf(activeColor));
                holder.vLine1.setBackgroundColor(activeColor);
                holder.ivStepOnSite.setImageTintList(ColorStateList.valueOf(activeColor));
                holder.vLine2.setBackgroundColor(activeColor);
            } else if ("resolved".equalsIgnoreCase(status)) {
                holder.tvLabelEnRoute.setTextColor(greyText);
                holder.tvLabelOnSite.setTextColor(greyText);
                holder.tvLabelResolved.setTextColor(activeColor);
                holder.ivStepEnRoute.setImageTintList(ColorStateList.valueOf(activeColor));
                holder.vLine1.setBackgroundColor(activeColor);
                holder.ivStepOnSite.setImageTintList(ColorStateList.valueOf(activeColor));
                holder.vLine2.setBackgroundColor(activeColor);
                holder.ivStepResolved.setImageTintList(ColorStateList.valueOf(activeColor));
                holder.vLine3.setBackgroundColor(activeColor);
            }

            if (report.getReportTime() != null) {
                CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                        report.getReportTime().getTime(),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS);
                holder.tvTime.setText(relativeTime);
            }

            if (report.getMergedCount() > 1) {
                holder.tvMerged.setVisibility(View.VISIBLE);
                holder.tvMerged.setText("+" + (report.getMergedCount() - 1) + " merged");
            } else {
                holder.tvMerged.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> listener.onItemClick(reportId));
        }

        @Override
        public int getItemCount() {
            return reports != null ? reports.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvType, tvLocation, tvTime, tvMerged;
            TextView tvLabelReported, tvLabelEnRoute, tvLabelOnSite, tvLabelResolved;
            ImageView ivStepReported, ivStepEnRoute, ivStepOnSite, ivStepResolved;
            View vLine1, vLine2, vLine3;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvType = itemView.findViewById(R.id.tv_report_type);
                tvLocation = itemView.findViewById(R.id.tv_report_location);
                tvTime = itemView.findViewById(R.id.tv_report_time);
                tvMerged = itemView.findViewById(R.id.tv_report_merged);
                
                tvLabelReported = itemView.findViewById(R.id.tv_label_reported);
                tvLabelEnRoute = itemView.findViewById(R.id.tv_label_en_route);
                tvLabelOnSite = itemView.findViewById(R.id.tv_label_on_site);
                tvLabelResolved = itemView.findViewById(R.id.tv_label_resolved);

                ivStepReported = itemView.findViewById(R.id.iv_step_reported);
                ivStepEnRoute = itemView.findViewById(R.id.iv_step_en_route);
                ivStepOnSite = itemView.findViewById(R.id.iv_step_on_site);
                ivStepResolved = itemView.findViewById(R.id.iv_step_resolved);
                
                vLine1 = itemView.findViewById(R.id.v_line_1);
                vLine2 = itemView.findViewById(R.id.v_line_2);
                vLine3 = itemView.findViewById(R.id.v_line_3);
            }
        }
    }
}
