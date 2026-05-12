package com.example.coordinaresponder.models;

import com.google.gson.annotations.SerializedName;

public class UserProfile {
    private String id;
    @SerializedName("full_name")
    private String fullName;
    private String phone;
    private String email;
    private String role;
    @SerializedName("password_hash")
    private String passwordHash;

    public UserProfile() {}

    public UserProfile(String id, String fullName, String email, String role, String passwordHash) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
