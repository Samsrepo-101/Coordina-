package com.example.coordinaresponder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
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
import com.example.coordinaresponder.network.SupabaseClient;
import com.example.coordinaresponder.network.SupabaseService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    private RecyclerView rvReports;
    private ReportAdapter adapter;
    private List<EmergencyReport> reportList;
    private SupabaseService supabaseService;
    private String token, userId;
    
    private TextView filterAll, filterActive, filterResolved;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        supabaseService = SupabaseClient.getService();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        token = prefs.getString("token", null);
        userId = prefs.getString("user_id", null);

        if (token == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvReports = findViewById(R.id.rv_reports);
        rvReports.setLayoutManager(new LinearLayoutManager(this));
        
        reportList = new ArrayList<>();
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
        Call<List<EmergencyReport>> call;
        String order = "reported_at.desc";

        if ("active".equals(filter)) {
            // Supabase filter: status in ('open', 'en route', 'on site')
            // Using a simple eq filter logic or multiple queries if needed, 
            // but for simplicity let's use the 'in' syntax if supported by the client or just handle locally.
            // Rest API syntax: ?status=in.(open,en route,on site)
            call = supabaseService.getIncidentsFiltered(Config.SUPABASE_ANON_KEY, token, "eq." + userId, "in.(open,en route,on site)", order);
        } else if ("resolved".equals(filter)) {
            call = supabaseService.getIncidentsFiltered(Config.SUPABASE_ANON_KEY, token, "eq." + userId, "eq.resolved", order);
        } else {
            call = supabaseService.getIncidents(Config.SUPABASE_ANON_KEY, token, "eq." + userId, order);
        }

        call.enqueue(new Callback<List<EmergencyReport>>() {
            @Override
            public void onResponse(Call<List<EmergencyReport>> call, Response<List<EmergencyReport>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reportList.clear();
                    reportList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<EmergencyReport>> call, Throwable t) {
                Toast.makeText(ReportActivity.this, "Failed to load reports", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
        private final List<EmergencyReport> reports;
        private final OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(String reportId);
        }

        public ReportAdapter(List<EmergencyReport> reports, OnItemClickListener listener) {
            this.reports = reports;
            this.listener = listener;
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

            holder.tvType.setText(report.getType() + " Emergency");
            holder.tvLocation.setText(report.getAddress());
            
            String status = report.getStatus();
            if (status == null) status = "open";

            int activeColor = 0xFFF44336; // red_main
            int inactiveColor = 0xFFE0E0E0;
            int greyText = 0xFF757575;

            holder.tvLabelEnRoute.setTextColor(inactiveColor);
            holder.tvLabelOnSite.setTextColor(inactiveColor);
            holder.tvLabelResolved.setTextColor(inactiveColor);

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
                holder.tvLabelOnSite.setTextColor(activeColor);
                holder.ivStepEnRoute.setImageTintList(ColorStateList.valueOf(activeColor));
                holder.vLine1.setBackgroundColor(activeColor);
                holder.ivStepOnSite.setImageTintList(ColorStateList.valueOf(activeColor));
                holder.vLine2.setBackgroundColor(activeColor);
            } else if ("resolved".equalsIgnoreCase(status)) {
                holder.tvLabelResolved.setTextColor(activeColor);
                holder.ivStepEnRoute.setImageTintList(ColorStateList.valueOf(activeColor));
                holder.vLine1.setBackgroundColor(activeColor);
                holder.ivStepOnSite.setImageTintList(ColorStateList.valueOf(activeColor));
                holder.vLine2.setBackgroundColor(activeColor);
                holder.ivStepResolved.setImageTintList(ColorStateList.valueOf(activeColor));
                holder.vLine3.setBackgroundColor(activeColor);
            }

            holder.tvTime.setText(report.getReportedAt() != null ? report.getReportedAt().substring(0, 10) : "");
            holder.itemView.setOnClickListener(v -> listener.onItemClick(report.getId()));
        }

        @Override
        public int getItemCount() {
            return reports.size();
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
                tvLabelEnRoute = itemView.findViewById(R.id.tv_label_en_route);
                tvLabelOnSite = itemView.findViewById(R.id.tv_label_on_site);
                tvLabelResolved = itemView.findViewById(R.id.tv_label_resolved);
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
