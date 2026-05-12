package com.example.coordinaresponder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coordinaresponder.models.UserProfile;
import com.example.coordinaresponder.network.SupabaseClient;
import com.example.coordinaresponder.network.SupabaseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {

    private SupabaseService supabaseService;
    private EditText etName, etUsername, etPhone, etAge, etAddress;
    private TextView tvEmailTop, tvDetailEmail;
    private Button btnSaveChanges, btnLogout;
    private ImageView btnEditProfile;
    private boolean isEditMode = false;
    private String token, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        
        supabaseService = SupabaseClient.getService();

        // Get session from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        token = prefs.getString("token", null);
        userId = prefs.getString("user_id", null);

        if (token == null || userId == null) {
            goToLogin();
            return;
        }

        // Initialize Views
        etName = findViewById(R.id.profile_name);
        tvEmailTop = findViewById(R.id.profile_email_top);
        tvDetailEmail = findViewById(R.id.detail_email);
        etUsername = findViewById(R.id.detail_username);
        etPhone = findViewById(R.id.detail_phone);
        etAge = findViewById(R.id.detail_age);
        etAddress = findViewById(R.id.detail_address);
        
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btnLogout = findViewById(R.id.btn_logout);

        loadUserData();

        btnEditProfile.setOnClickListener(v -> toggleEditMode(!isEditMode));

        btnSaveChanges.setOnClickListener(v -> saveChanges());

        btnLogout.setOnClickListener(v -> {
            logout();
        });

        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.nav_report).setOnClickListener(v -> {
            startActivity(new Intent(this, ReportActivity.class));
            finish();
        });
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void toggleEditMode(boolean enable) {
        isEditMode = enable;
        etName.setEnabled(enable);
        etUsername.setEnabled(enable);
        etPhone.setEnabled(enable);
        etAge.setEnabled(enable);
        etAddress.setEnabled(enable);

        if (enable) {
            btnSaveChanges.setVisibility(View.VISIBLE);
            btnEditProfile.setAlpha(0.5f);
            Toast.makeText(this, "Edit Mode Enabled", Toast.LENGTH_SHORT).show();
        } else {
            btnSaveChanges.setVisibility(View.GONE);
            btnEditProfile.setAlpha(1.0f);
        }
    }

    private void saveChanges() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        // Note: The schema uses 'phone' and 'full_name'
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("full_name", name);
        updates.put("phone", phone);
        // Add more fields if they exist in your public.users table

        supabaseService.updateUserProfile(Config.SUPABASE_ANON_KEY, token, "eq." + userId, updates)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AccountActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            toggleEditMode(false);
                            // Update local name if changed
                            getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().putString("user_name", name).apply();
                        } else {
                            Toast.makeText(AccountActivity.this, "Update Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AccountActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserData() {
        supabaseService.getUserProfile(Config.SUPABASE_ANON_KEY, token, "eq." + userId)
                .enqueue(new Callback<List<UserProfile>>() {
                    @Override
                    public void onResponse(Call<List<UserProfile>> call, Response<List<UserProfile>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            UserProfile profile = response.body().get(0);
                            etName.setText(profile.getFullName());
                            tvEmailTop.setText(profile.getEmail());
                            tvDetailEmail.setText(profile.getEmail());
                            etPhone.setText(profile.getPhone());
                            // Add other fields as necessary based on schema
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserProfile>> call, Throwable t) {
                        Toast.makeText(AccountActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
