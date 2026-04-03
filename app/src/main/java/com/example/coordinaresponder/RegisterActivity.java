package com.example.coordinaresponder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coordinaresponder.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etFullName, etEmail, etPassword, etAge, etPhone, etAddress;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etUsername = findViewById(R.id.et_reg_username);
        etFullName = findViewById(R.id.et_reg_full_name);
        etEmail = findViewById(R.id.et_reg_email);
        etPassword = findViewById(R.id.et_reg_password);
        etAge = findViewById(R.id.et_reg_age);
        etPhone = findViewById(R.id.et_reg_phone);
        etAddress = findViewById(R.id.et_reg_address);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> registerUser());

        findViewById(R.id.tv_go_to_login).setOnClickListener(v -> {
            finish();
        });
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        long age;
        try {
            age = Long.parseLong(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid age", Toast.LENGTH_SHORT).show();
            return;
        }

        String verificationCode = String.valueOf(100000 + new Random().nextInt(900000));

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        
                        User newUser = new User(
                                username,
                                email,
                                password,
                                fullName,
                                age,
                                address,
                                phone,
                                verificationCode
                        );

                        db.collection("users").document(userId)
                                .set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finishAffinity();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegisterActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
