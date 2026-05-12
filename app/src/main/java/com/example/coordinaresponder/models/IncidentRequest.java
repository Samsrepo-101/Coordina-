package com.example.coordinaresponder.models;

import com.google.gson.annotations.SerializedName;

public class IncidentRequest {
    private String type;
    private String status;
    private double latitude;
    private double longitude;
    private String address;
    @SerializedName("reported_by")
    private String reportedBy;

    public IncidentRequest(String type, String status, double latitude, double longitude, String address, String reportedBy) {
        this.type = type;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.reportedBy = reportedBy;
    }

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }
}
