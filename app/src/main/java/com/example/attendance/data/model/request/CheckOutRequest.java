package com.example.attendance.data.model.request;

public class CheckOutRequest {
    private String eventId;
    private String sessionId;
    private String timestamp;
    private WifiEvidence wifi;
    private LocationEvidence location;

    private String deviceId;
    private String appVersion;

    public CheckOutRequest(String eventId, String sessionId, String timestamp, WifiEvidence wifi, LocationEvidence location) {
        this(eventId, sessionId, timestamp, wifi, location, null, "1.0.0");
    }

    public CheckOutRequest(String eventId, String sessionId, String timestamp, WifiEvidence wifi, LocationEvidence location, String deviceId, String appVersion) {
        this.eventId = eventId;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.wifi = wifi;
        this.location = location;
        this.deviceId = deviceId;
        this.appVersion = appVersion;
    }

    public String getEventId() { return eventId; }
    public String getSessionId() { return sessionId; }
    public String getTimestamp() { return timestamp; }
    public WifiEvidence getWifi() { return wifi; }
    public LocationEvidence getLocation() { return location; }
    public String getDeviceId() { return deviceId; }
    public String getAppVersion() { return appVersion; }
}
