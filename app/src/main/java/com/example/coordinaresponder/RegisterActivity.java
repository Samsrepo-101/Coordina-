package com.example.coordinaresponder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coordinaresponder.models.SignupRequest;
import com.example.coordinaresponder.models.SupabaseAuthResponse;
import com.example.coordinaresponder.models.UserProfile;
import com.example.coordinaresponder.network.SupabaseClient;
import com.example.coordinaresponder.network.SupabaseService;
import com.example.coordinaresponder.utils.HashUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etPhone;
    private Button btnRegister;
    private SupabaseService supabaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        supabaseService = SupabaseClient.getService();

        etFullName = findViewById(R.id.et_reg_full_name);
        etEmail = findViewById(R.id.et_reg_email);
        etPassword = findViewById(R.id.et_reg_password);
        etPhone = findViewById(R.id.et_reg_phone);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> registerUser());

        findViewById(R.id.tv_go_to_login).setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Step A: Supabase Auth signup
        SignupRequest signupRequest = new SignupRequest(email, password);

        supabaseService.signUp(Config.SUPABASE_ANON_KEY, signupRequest).enqueue(new Callback<SupabaseAuthResponse>() {
            @Override
            public void onResponse(Call<SupabaseAuthResponse> call, Response<SupabaseAuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String userId = response.body().getUser().getId();
                    String accessToken = "Bearer " + response.body().getAccessToken();

                    // 2. Step B: Insert into public.users with hashed password
                    String passwordHash = HashUtils.sha256(password);
                    
                    // ROLE: Using "user" for the User App (Incident Reporter)
                    UserProfile profile = new UserProfile(userId, fullName, email, "user", passwordHash);
                    profile.setPhone(phone);

                    supabaseService.createUserProfile(Config.SUPABASE_ANON_KEY, accessToken, profile).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> profileResponse) {
                            if (profileResponse.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registration Successful! Please login.", Toast.LENGTH_LONG).show();
                                // FIX: Redirect to Login screen, no auto-login
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Failed to create profile: " + profileResponse.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(RegisterActivity.this, "Profile Creation Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Signup failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SupabaseAuthResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Signup failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
