package com.example.attendance.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.example.attendance.data.model.request.LocationEvidence;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

public class LocationService {
    private static final String TAG = "LocationService";
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;

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
        boolean isMock = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isMock = location.isMock();
        } else {
            isMock = location.isFromMockProvider();
        }

        LocationEvidence evidence = new LocationEvidence(
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                isMock
        );
        callback.onLocationRetrieved(evidence);
    }
}

