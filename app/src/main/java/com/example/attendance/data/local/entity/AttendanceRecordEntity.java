package com.example.attendance.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "attendance_records")
public class AttendanceRecordEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String eventType;      // "CHECK_IN" hoặc "CHECK_OUT"
    private String timestamp;      // ISO8601 string
    private String dateString;     // e.g. "17/09/2026"
    private String timeString;     // e.g. "08:15:30"
    private boolean faceVerified;
    private boolean gpsVerified;
    private boolean wifiVerified;
    private double latitude;
    private double longitude;
    private String wifiSsid;
    private String statusMessage;  // e.g. "THÀNH CÔNG" / "LƯU NGOẠI TUYẾN"
    private String faceImagePath;  // Path to captured face photo file

    public AttendanceRecordEntity(@NonNull String id, String eventType, String timestamp, String dateString, String timeString, boolean faceVerified, boolean gpsVerified, boolean wifiVerified, double latitude, double longitude, String wifiSsid, String statusMessage, String faceImagePath) {
        this.id = id;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.dateString = dateString;
        this.timeString = timeString;
        this.faceVerified = faceVerified;
        this.gpsVerified = gpsVerified;
        this.wifiVerified = wifiVerified;
        this.latitude = latitude;
        this.longitude = longitude;
        this.wifiSsid = wifiSsid;
        this.statusMessage = statusMessage;
        this.faceImagePath = faceImagePath;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getDateString() { return dateString; }
    public void setDateString(String dateString) { this.dateString = dateString; }

    public String getTimeString() { return timeString; }
    public void setTimeString(String timeString) { this.timeString = timeString; }

    public boolean isFaceVerified() { return faceVerified; }
    public void setFaceVerified(boolean faceVerified) { this.faceVerified = faceVerified; }

    public boolean isGpsVerified() { return gpsVerified; }
    public void setGpsVerified(boolean gpsVerified) { this.gpsVerified = gpsVerified; }

    public boolean isWifiVerified() { return wifiVerified; }
    public void setWifiVerified(boolean wifiVerified) { this.wifiVerified = wifiVerified; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getWifiSsid() { return wifiSsid; }
    public void setWifiSsid(String wifiSsid) { this.wifiSsid = wifiSsid; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public String getFaceImagePath() { return faceImagePath; }
    public void setFaceImagePath(String faceImagePath) { this.faceImagePath = faceImagePath; }
}
