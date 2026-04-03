package com.example.coordinaresponder.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class User {
    private String username;
    private String email;
    private String passwordHash; // Added password_hash
    private String fullName;
    private long age;
    private String address;
    private String phoneNumber;
    private boolean isVerified;
    private String verificationCode;
    @ServerTimestamp
    private Date createdAt;

    public User() {} // Required for Firestore

    public User(String username, String email, String passwordHash, String fullName, long age, String address, String phoneNumber, String verificationCode) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.age = age;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.isVerified = false;
        this.verificationCode = verificationCode;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public long getAge() { return age; }
    public void setAge(long age) { this.age = age; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
