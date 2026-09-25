package com.example.attendance.data.repository;

import android.content.Context;

import com.example.attendance.data.api.AttendanceApi;
import com.example.attendance.data.api.RetrofitClient;
import com.example.attendance.data.local.AppDatabase;
import com.example.attendance.data.local.entity.AttendanceRecordEntity;
import com.example.attendance.data.local.entity.PendingEventEntity;
import com.example.attendance.data.model.request.CheckInRequest;
import com.example.attendance.data.model.request.CheckOutRequest;
import com.example.attendance.data.model.request.FaceEvidence;
import com.example.attendance.data.model.request.LocationEvidence;
import com.example.attendance.data.model.request.WifiEvidence;
import com.example.attendance.data.model.response.CheckInResponse;
import com.example.attendance.data.model.response.CheckOutResponse;
import com.example.attendance.service.WifiService;
import com.example.attendance.utils.AttendanceVerifier;
import com.example.attendance.utils.SessionManager;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceRepository {
    private final Context context;
    private final AttendanceApi attendanceApi;
    private final SessionManager sessionManager;
    private final WifiService wifiService;

    public interface AttendanceCallback<T> {
        void onSuccess(T result);
        void onError(String message);
        void onSavedOffline(String eventId);
    }

    public interface LocalRecordsCallback {
        void onLoaded(List<AttendanceRecordEntity> records);
    }

    public AttendanceRepository(Context context) {
        this.context = context.getApplicationContext();
        this.attendanceApi = RetrofitClient.getClient(context).create(AttendanceApi.class);
        this.sessionManager = new SessionManager(context);
        this.wifiService = new WifiService(context);
    }

    public void checkIn(FaceEvidence faceEvidence, LocationEvidence locationEvidence, String faceImagePath, AttendanceCallback<CheckInResponse> callback) {
        WifiEvidence wifiEvidence = wifiService.collectWifiEvidence();
        String eventId = UUID.randomUUID().toString();
        String timestamp = getCurrentISO8601Timestamp();
        String dateStr = getCurrentDateString();
        String timeStr = getCurrentTimeString();

        AttendanceVerifier.VerificationResult verifyResult = AttendanceVerifier.verify(faceEvidence, locationEvidence, wifiEvidence);

        String deviceId = sessionManager.getDeviceId();
        CheckInRequest request = new CheckInRequest(eventId, timestamp, faceEvidence, wifiEvidence, locationEvidence, deviceId, "1.0.0");

        // Lưu bản ghi vào CSDL Room DB ngay lập tức
        saveAttendanceRecordLocal("CHECK_IN", eventId, timestamp, dateStr, timeStr, verifyResult, wifiEvidence, locationEvidence, faceImagePath);

        if (!wifiService.isInternetReachable()) {
            saveEventOffline("CHECK_IN", request.getEventId(), request.getTimestamp(), new Gson().toJson(request));
            com.example.attendance.service.AttendanceMonitoringService.startService(context);
            callback.onSavedOffline(eventId);
            return;
        }

        attendanceApi.checkIn(request).enqueue(new Callback<CheckInResponse>() {
            @Override
            public void onResponse(Call<CheckInResponse> call, Response<CheckInResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CheckInResponse res = response.body();
                    if (res.isSuccess() && res.getSessionId() != null) {
                        sessionManager.saveCurrentSessionId(res.getSessionId());
                    }
                    com.example.attendance.service.AttendanceMonitoringService.startService(context);
                    callback.onSuccess(res);
                } else {
                    saveEventOffline("CHECK_IN", request.getEventId(), request.getTimestamp(), new Gson().toJson(request));
                    com.example.attendance.service.AttendanceMonitoringService.startService(context);
                    callback.onSavedOffline(eventId);
                }
            }

            @Override
            public void onFailure(Call<CheckInResponse> call, Throwable t) {
                saveEventOffline("CHECK_IN", request.getEventId(), request.getTimestamp(), new Gson().toJson(request));
                com.example.attendance.service.AttendanceMonitoringService.startService(context);
                callback.onSavedOffline(eventId);
            }
        });
    }

    public void checkOut(LocationEvidence locationEvidence, AttendanceCallback<CheckOutResponse> callback) {
        String sessionId = sessionManager.getCurrentSessionId();
        String deviceId = sessionManager.getDeviceId();
        WifiEvidence wifiEvidence = wifiService.collectWifiEvidence();
        String eventId = UUID.randomUUID().toString();
        String timestamp = getCurrentISO8601Timestamp();
        String dateStr = getCurrentDateString();
        String timeStr = getCurrentTimeString();

        AttendanceVerifier.VerificationResult verifyResult = AttendanceVerifier.verify(null, locationEvidence, wifiEvidence);

        CheckOutRequest request = new CheckOutRequest(eventId, sessionId, timestamp, wifiEvidence, locationEvidence, deviceId, "1.0.0");

        // Lưu bản ghi vào CSDL Room DB
        saveAttendanceRecordLocal("CHECK_OUT", eventId, timestamp, dateStr, timeStr, verifyResult, wifiEvidence, locationEvidence, null);

        com.example.attendance.service.AttendanceMonitoringService.stopService(context);

        if (!wifiService.isInternetReachable()) {
            saveEventOffline("CHECK_OUT", request.getEventId(), request.getTimestamp(), new Gson().toJson(request));
            sessionManager.saveCurrentSessionId(null);
            callback.onSavedOffline(eventId);
            return;
        }

        attendanceApi.checkOut(request).enqueue(new Callback<CheckOutResponse>() {
            @Override
            public void onResponse(Call<CheckOutResponse> call, Response<CheckOutResponse> response) {
                sessionManager.saveCurrentSessionId(null);
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    saveEventOffline("CHECK_OUT", request.getEventId(), request.getTimestamp(), new Gson().toJson(request));
                    callback.onSavedOffline(eventId);
                }
            }

            @Override
            public void onFailure(Call<CheckOutResponse> call, Throwable t) {
                saveEventOffline("CHECK_OUT", request.getEventId(), request.getTimestamp(), new Gson().toJson(request));
            }
        });
    }

    public void temporaryOut(String reason, FaceEvidence faceEvidence, LocationEvidence locationEvidence, AttendanceCallback<Void> callback) {
        String eventId = UUID.randomUUID().toString();
        String timestamp = getCurrentISO8601Timestamp();
        String dateStr = getCurrentDateString();
        String timeStr = getCurrentTimeString();
        WifiEvidence wifiEvidence = wifiService.collectWifiEvidence();

        AttendanceVerifier.VerificationResult verifyResult = AttendanceVerifier.verify(faceEvidence, locationEvidence, wifiEvidence);
        saveAttendanceRecordLocal("TEMP_OUT", eventId, timestamp, dateStr, timeStr, verifyResult, wifiEvidence, locationEvidence, null);
        callback.onSuccess(null);
    }

    public void temporaryIn(FaceEvidence faceEvidence, LocationEvidence locationEvidence, AttendanceCallback<Void> callback) {
        String eventId = UUID.randomUUID().toString();
        String timestamp = getCurrentISO8601Timestamp();
        String dateStr = getCurrentDateString();
        String timeStr = getCurrentTimeString();
        WifiEvidence wifiEvidence = wifiService.collectWifiEvidence();

        AttendanceVerifier.VerificationResult verifyResult = AttendanceVerifier.verify(faceEvidence, locationEvidence, wifiEvidence);
        saveAttendanceRecordLocal("TEMP_IN", eventId, timestamp, dateStr, timeStr, verifyResult, wifiEvidence, locationEvidence, null);
        callback.onSuccess(null);
    }

    public void getLocalAttendanceRecords(LocalRecordsCallback callback) {
        new Thread(() -> {
            List<AttendanceRecordEntity> records = AppDatabase.getInstance(context).attendanceRecordDao().getAllRecords();
            callback.onLoaded(records);
        }).start();
    }

    private void saveAttendanceRecordLocal(String type, String eventId, String timestamp, String dateStr, String timeStr, AttendanceVerifier.VerificationResult verifyResult, WifiEvidence wifi, LocationEvidence loc, String faceImagePath) {
        new Thread(() -> {
            AttendanceRecordEntity entity = new AttendanceRecordEntity(
                    eventId,
                    type,
                    timestamp,
                    dateStr,
                    timeStr,
                    verifyResult.isFacePassed(),
                    verifyResult.isGpsPassed(),
                    verifyResult.isWifiPassed(),
                    loc != null ? loc.getLatitude() : 0.0,
                    loc != null ? loc.getLongitude() : 0.0,
                    wifi != null ? wifi.getSsid() : "N/A",
                    "THÀNH CÔNG",
                    faceImagePath
            );
            AppDatabase.getInstance(context).attendanceRecordDao().insert(entity);
        }).start();
    }

    private void saveEventOffline(String eventType, String eventId, String timestamp, String payloadJson) {
        new Thread(() -> {
            PendingEventEntity entity = new PendingEventEntity(
                    eventId,
                    eventType,
                    payloadJson,
                    timestamp,
                    "PENDING",
                    0
            );
            AppDatabase.getInstance(context).pendingEventDao().insert(entity);
        }).start();
    }

    private String getCurrentISO8601Timestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }

    private String getCurrentDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getCurrentTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
