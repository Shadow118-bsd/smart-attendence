package com.example.attendance.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.example.attendance.data.model.request.LocationEvidence;
import com.example.attendance.utils.AttendanceVerifier;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

public class LocationService {
    private static final String TAG = "LocationService";
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;

    // Lưu vị trí và thời gian gần nhất để thực thi thuật toán Teleportation Anomaly Check
    private static Location lastRetrievedLocation = null;
    private static long lastRetrievedTimestamp = 0;

    public interface LocationCallback {
        void onLocationRetrieved(LocationEvidence locationEvidence);
    }

    public LocationService(Context context) {
        this.context = context.getApplicationContext();
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(LocationCallback callback) {
        try {
            CancellationTokenSource cts = new CancellationTokenSource();
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken())
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            processAndNotifyLocation(location, callback);
                        } else {
                            Log.w(TAG, "getCurrentLocation is null, falling back to getLastLocation");
                            fallbackToLastLocation(callback);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "getCurrentLocation failed: " + e.getMessage() + ", falling back to getLastLocation");
                        fallbackToLastLocation(callback);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception getting location: " + e.getMessage());
            callback.onLocationRetrieved(new LocationEvidence(0.0, 0.0, 999.0f, false));
        }
    }

    @SuppressLint("MissingPermission")
    private void fallbackToLastLocation(LocationCallback callback) {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    processAndNotifyLocation(location, callback);
                } else {
                    Log.w(TAG, "Last location is also null, returning default unknown accuracy evidence");
                    callback.onLocationRetrieved(new LocationEvidence(0.0, 0.0, 999.0f, false));
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to get last location: " + e.getMessage());
                callback.onLocationRetrieved(new LocationEvidence(0.0, 0.0, 999.0f, false));
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in fallback location: " + e.getMessage());
            callback.onLocationRetrieved(new LocationEvidence(0.0, 0.0, 999.0f, false));
        }
    }

    private void processAndNotifyLocation(Location location, LocationCallback callback) {
        boolean isMock = detectMultiTierMockLocation(location);

        LocationEvidence evidence = new LocationEvidence(
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                isMock
        );
        callback.onLocationRetrieved(evidence);
    }

    /**
     * Thuật toán kiểm tra Fake GPS / Mock Location đa tầng (Multi-tier Mock Detection).
     * Level 1: Android SDK isMock() / isFromMockProvider()
     * Level 2: Location Extras Inspection
     * Level 3: System Settings Check (Mock Location Setting)
     * Level 4: Teleportation Speed Anomaly Check (Di chuyển bất thường > 150 km/h)
     */
    private boolean detectMultiTierMockLocation(Location location) {
        if (location == null) return false;

        // Level 1: Standard SDK Check
        boolean isMock = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isMock = location.isMock();
        } else {
            isMock = location.isFromMockProvider();
        }

        if (isMock) {
            Log.w(TAG, "Mock Location detected by Level 1 SDK check.");
            return true;
        }

        // Level 2: Extras Inspection
        Bundle extras = location.getExtras();
        if (extras != null && extras.getBoolean("mockLocation", false)) {
            Log.w(TAG, "Mock Location detected by Level 2 Extras check.");
            return true;
        }

        // Level 3: Legacy System Settings Check
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                String mockSetting = Settings.Secure.getString(context.getContentResolver(), "mock_location");
                if (!"0".equals(mockSetting)) {
                    Log.w(TAG, "Mock Location detected by Level 3 System Settings check.");
                    return true;
                }
            }
        } catch (Exception ignored) { }

        // Level 4: Teleportation Speed Anomaly Check
        long currentTime = System.currentTimeMillis();
        if (lastRetrievedLocation != null && lastRetrievedTimestamp > 0) {
            long timeDiffSeconds = (currentTime - lastRetrievedTimestamp) / 1000;
            if (timeDiffSeconds > 0 && timeDiffSeconds < 60) {
                double distanceMeters = AttendanceVerifier.calculateHaversineDistance(
                        lastRetrievedLocation.getLatitude(),
                        lastRetrievedLocation.getLongitude(),
                        location.getLatitude(),
                        location.getLongitude()
                );
                double speedMps = distanceMeters / timeDiffSeconds;
                double speedKmh = speedMps * 3.6;

                // Nếu vận tốc di chuyển giữa 2 lần lấy GPS vượt quá 150 km/h -> Dịch chuyển bất thường (Fake GPS)
                if (speedKmh > 150.0 && distanceMeters > 500.0) {
                    Log.w(TAG, "Mock Location detected by Level 4 Teleportation Anomaly: " + Math.round(speedKmh) + " km/h");
                    return true;
                }
            }
        }

        // Cập nhật mốc thời gian và tọa độ gần nhất
        lastRetrievedLocation = location;
        lastRetrievedTimestamp = currentTime;

        return false;
    }
}
