package com.example.coordinaresponder.models;

import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class EmergencyReport {
    private String userId;
    private String emergencyType;
    private String location;
    private String details;
    private String photoUrl;
    @ServerTimestamp
    private Date reportTime;
    private Date enRouteTime;
    private Date onSiteTime;
    private Date resolvedTime;
    private String status;
    private int mergedCount;

    public EmergencyReport() {}

    public EmergencyReport(String userId, String emergencyType, String location, String details, String photoUrl) {
        this.userId = userId;
        this.emergencyType = emergencyType;
        this.location = location;
        this.details = details;
        this.photoUrl = photoUrl;
        this.status = "pending";
        this.mergedCount = 1;
    }

    @PropertyName("user_id")
    public String getUserId() { return userId; }
    @PropertyName("user_id")
    public void setUserId(String userId) { this.userId = userId; }

    @PropertyName("emergency_type")
    public String getEmergencyType() { return emergencyType; }
    @PropertyName("emergency_type")
    public void setEmergencyType(String emergencyType) { this.emergencyType = emergencyType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    @PropertyName("photo_url")
    public String getPhotoUrl() { return photoUrl; }
    @PropertyName("photo_url")
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    @PropertyName("report_time")
    public Date getReportTime() { return reportTime; }
    @PropertyName("report_time")
    public void setReportTime(Date reportTime) { this.reportTime = reportTime; }

    @PropertyName("en_route_time")
    public Date getEnRouteTime() { return enRouteTime; }
    @PropertyName("en_route_time")
    public void setEnRouteTime(Date enRouteTime) { this.enRouteTime = enRouteTime; }

    @PropertyName("on_site_time")
    public Date getOnSiteTime() { return onSiteTime; }
    @PropertyName("on_site_time")
    public void setOnSiteTime(Date onSiteTime) { this.onSiteTime = onSiteTime; }

    @PropertyName("resolved_time")
    public Date getResolvedTime() { return resolvedTime; }
    @PropertyName("resolved_time")
    public void setResolvedTime(Date resolvedTime) { this.resolvedTime = resolvedTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @PropertyName("merged_count")
    public int getMergedCount() { return mergedCount; }
    @PropertyName("merged_count")
    public void setMergedCount(int mergedCount) { this.mergedCount = mergedCount; }
}
