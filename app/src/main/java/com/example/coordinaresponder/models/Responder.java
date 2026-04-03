package com.example.coordinaresponder.models;

public class Responder {
    private String username;
    private String role;
    private String hotlineNumber;
    private String organizationName;

    public Responder() {}

    public Responder(String username, String role, String hotlineNumber, String organizationName) {
        this.username = username;
        this.role = role;
        this.hotlineNumber = hotlineNumber;
        this.organizationName = organizationName;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getHotlineNumber() { return hotlineNumber; }
    public void setHotlineNumber(String hotlineNumber) { this.hotlineNumber = hotlineNumber; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
}
