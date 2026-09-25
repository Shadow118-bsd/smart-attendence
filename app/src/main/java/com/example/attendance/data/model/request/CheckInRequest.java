package com.example.attendance.data.model.request;

public class CheckInRequest {
    private String eventId;
    private String timestamp;
    private FaceEvidence face;
    private WifiEvidence wifi;
    private LocationEvidence location;

    private String deviceId;
    private String appVersion;

    public CheckInRequest(String eventId, String timestamp, FaceEvidence face, WifiEvidence wifi, LocationEvidence location) {
        this(eventId, timestamp, face, wifi, location, null, "1.0.0");
    }

    public CheckInRequest(String eventId, String timestamp, FaceEvidence face, WifiEvidence wifi, LocationEvidence location, String deviceId, String appVersion) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.face = face;
        this.wifi = wifi;
        this.location = location;
        this.deviceId = deviceId;
        this.appVersion = appVersion;
    }

    public String getEventId() { return eventId; }
    public String getTimestamp() { return timestamp; }
    public FaceEvidence getFace() { return face; }
    public WifiEvidence getWifi() { return wifi; }
    public LocationEvidence getLocation() { return location; }
    public String getDeviceId() { return deviceId; }
    public String getAppVersion() { return appVersion; }
}
