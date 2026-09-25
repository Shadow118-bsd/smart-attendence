package com.example.attendance.data.model.request;

public class LocationEvidence {
    private double latitude;
    private double longitude;
    private float accuracy;
    private boolean isMock;

    private String provider;
    private float speed;
    private double altitude;

    public LocationEvidence(double latitude, double longitude, float accuracy) {
        this(latitude, longitude, accuracy, false, "gps", 0f, 0.0);
    }

    public LocationEvidence(double latitude, double longitude, float accuracy, boolean isMock) {
        this(latitude, longitude, accuracy, isMock, "gps", 0f, 0.0);
    }

    public LocationEvidence(double latitude, double longitude, float accuracy, boolean isMock, String provider, float speed, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.isMock = isMock;
        this.provider = provider;
        this.speed = speed;
        this.altitude = altitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public float getAccuracy() { return accuracy; }
    public boolean isMock() { return isMock; }
    public String getProvider() { return provider; }
    public float getSpeed() { return speed; }
    public double getAltitude() { return altitude; }
}

