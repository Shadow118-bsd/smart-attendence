package com.example.attendance.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.attendance.data.api.AttendanceApi;
import com.example.attendance.data.api.RetrofitClient;
import com.example.attendance.data.local.AppDatabase;
import com.example.attendance.data.local.entity.PendingEventEntity;
import com.example.attendance.data.model.request.HeartbeatRequest;
import com.example.attendance.data.model.request.WifiEvidence;
import com.example.attendance.data.model.response.HeartbeatResponse;
import com.example.attendance.service.LocationService;
import com.example.attendance.service.WifiService;
import com.example.attendance.utils.SessionManager;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import retrofit2.Response;

public class HeartbeatWorker extends Worker {
    private static final String TAG = "HeartbeatWorker";

    public HeartbeatWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        SessionManager sessionManager = new SessionManager(context);
        String sessionId = sessionManager.getCurrentSessionId();

        if (sessionId == null || sessionId.isEmpty()) {
            Log.d(TAG, "No active attendance session, skipping heartbeat.");
            return Result.success();
        }

        WifiService wifiService = new WifiService(context);
        WifiEvidence wifiEvidence = wifiService.collectWifiEvidence();

        String eventId = UUID.randomUUID().toString();
        String timestamp = getCurrentISO8601Timestamp();

        LocationService locationService = new LocationService(context);
        locationService.getCurrentLocation(locationEvidence -> {
            HeartbeatRequest request = new HeartbeatRequest(eventId, sessionId, timestamp, wifiEvidence, locationEvidence);

            if (wifiService.isInternetReachable()) {
                try {
                    AttendanceApi api = RetrofitClient.getClient(context).create(AttendanceApi.class);
                    Response<HeartbeatResponse> response = api.heartbeat(request).execute();
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Heartbeat sent successfully.");
                    } else {
                        saveOfflineHeartbeat(context, request);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Network error during heartbeat: " + e.getMessage());
                    saveOfflineHeartbeat(context, request);
                }
            } else {
                Log.d(TAG, "No internet connection, saving heartbeat to Room Database queue.");
                saveOfflineHeartbeat(context, request);
            }
        });

        return Result.success();
    }

    private void saveOfflineHeartbeat(Context context, HeartbeatRequest request) {
        new Thread(() -> {
            try {
                String payloadJson = new Gson().toJson(request);
                PendingEventEntity entity = new PendingEventEntity(
                        request.getEventId(),
                        "HEARTBEAT",
                        payloadJson,
                        request.getTimestamp(),
                        "PENDING",
                        0
                );
                AppDatabase.getInstance(context).pendingEventDao().insert(entity);
            } catch (Exception e) {
                Log.e(TAG, "Error saving offline heartbeat: " + e.getMessage());
            }
        }).start();
    }

    private String getCurrentISO8601Timestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }
}
