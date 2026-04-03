package com.example.coordinaresponder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText etName, etUsername, etPhone, etAge, etAddress;
    private TextView tvEmailTop, tvDetailEmail;
    private Button btnSaveChanges, btnLogout;
    private ImageView btnEditProfile;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
            mAuth.signOut();
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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
        if (mAuth.getCurrentUser() == null) return;

        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Name and Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", name);
        updates.put("username", username);
        updates.put("phoneNumber", phone);
        updates.put("address", address);
        
        try {
            if (!ageStr.isEmpty()) {
                updates.put("age", Long.parseLong(ageStr));
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid age format", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AccountActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    toggleEditMode(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AccountActivity.this, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserData() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            String email = mAuth.getCurrentUser().getEmail();
            
            tvEmailTop.setText(email);
            tvDetailEmail.setText(email);

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            etName.setText(doc.getString("fullName"));
                            etUsername.setText(doc.getString("username"));
                            etPhone.setText(doc.getString("phoneNumber"));
                            etAddress.setText(doc.getString("address"));
                            
                            Long age = doc.getLong("age");
                            if (age != null) etAge.setText(String.valueOf(age));
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
