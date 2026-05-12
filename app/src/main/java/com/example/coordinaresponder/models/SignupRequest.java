package com.example.coordinaresponder.models;

public class SignupRequest {
    private String email;
    private String password;

    public SignupRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
