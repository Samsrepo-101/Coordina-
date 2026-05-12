package com.example.coordinaresponder.models;

import com.google.gson.annotations.SerializedName;

public class SupabaseAuthResponse {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("user")
    private UserData user;

    public String getAccessToken() { return accessToken; }
    public UserData getUser() { return user; }

    public static class UserData {
        private String id;
        private String email;
        public String getId() { return id; }
        public String getEmail() { return email; }
    }
}
