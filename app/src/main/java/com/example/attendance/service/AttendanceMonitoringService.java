package com.example.attendance.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.attendance.MainActivity;
import com.example.attendance.data.api.AttendanceApi;
import com.example.attendance.data.api.RetrofitClient;
import com.example.attendance.data.model.request.HeartbeatRequest;
import com.example.attendance.data.model.request.LocationEvidence;
import com.example.attendance.data.model.request.WifiEvidence;
import com.example.attendance.data.model.response.HeartbeatResponse;
import com.example.attendance.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceMonitoringService extends Service {
    private static final String TAG = "AttendanceMonitorSvc";
    private static final String CHANNEL_ID = "AttendanceMonitoringChannel";
    private static final String WARNING_CHANNEL_ID = "AttendanceWarningChannel";
    private static final int NOTIFICATION_ID = 1001;
    private static final int WARNING_NOTIFICATION_ID = 2002;
    private static final long HEARTBEAT_INTERVAL_MS = 3 * 60 * 1000; // 3 minutes

    private Handler handler;
    private Runnable heartbeatRunnable;
    private WifiService wifiService;
    private LocationService locationService;
    private SessionManager sessionManager;
    private AttendanceApi attendanceApi;

    public static void startService(Context context) {
        Intent intent = new Intent(context, AttendanceMonitoringService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, AttendanceMonitoringService.class);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate()    ;
        wifiService = new WifiService(this);
        locationService = new LocationService(this);
        sessionManager = new SessionManager(this);
        attendanceApi = RetrofitClient.getClient(this).create(AttendanceApi.class);

        createNotificationChannels();
        startForeground(NOTIFICATION_ID, buildForegroundNotification("Đang giám sát trạng thái có mặt"));

        handler = new Handler(Looper.getMainLooper());
        heartbeatRunnable = new Runnable() {
            @Override
            public void run() {
                sendHeartbeat();
                handler.postDelayed(this, HEARTBEAT_INTERVAL_MS);
            }
        };
        handler.post(heartbeatRunnable);
    }

    private void sendHeartbeat() {
        String sessionId = sessionManager.getCurrentSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            Log.d(TAG, "No active session ID. Skipping heartbeat.");
            return;
        }

        WifiEvidence wifiEvidence = wifiService.collectWifiEvidence();
        locationService.getCurrentLocation(new LocationService.LocationCallback() {
            @Override
            public void onLocationRetrieved(LocationEvidence locEvidence) {
                String eventId = UUID.randomUUID().toString();
                String timestamp = getCurrentISO8601Timestamp();
                String deviceId = sessionManager.getDeviceId();

                HeartbeatRequest req = new HeartbeatRequest(eventId, sessionId, timestamp, wifiEvidence, locEvidence, deviceId, "1.0.0");

                attendanceApi.heartbeat(req).enqueue(new Callback<HeartbeatResponse>() {
                    @Override
                    public void onResponse(Call<HeartbeatResponse> call, Response<HeartbeatResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            HeartbeatResponse res = response.body();
                            handleHeartbeatResult(res);
                        }
                    }

                    @Override
                    public void onFailure(Call<HeartbeatResponse> call, Throwable t) {
                        Log.e(TAG, "Heartbeat failed: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void handleHeartbeatResult(HeartbeatResponse response) {
        String status = response.getStatus();
        if ("TEMPORARILY_AWAY".equals(status) || "OUT_OF_BOUNDS".equals(status) || "WARNING".equals(status)) {
            triggerWarningNotification("Cảnh báo hiện diện!", "Bạn đang ngoài khu vực làm việc/Wi-Fi công ty. Vui lòng quay lại vị trí.");
        }
    }

    private void triggerWarningNotification(String title, String message) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, WARNING_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        manager.notify(WARNING_NOTIFICATION_ID, notification);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Ghi nhận hiện diện ngầm",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationChannel warningChannel = new NotificationChannel(
                    WARNING_CHANNEL_ID,
                    "Cảnh báo Điểm danh & Vùng làm việc",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
                manager.createNotificationChannel(warningChannel);
            }
        }
    }

    private Notification buildForegroundNotification(String contentText) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Smart Attendance Monitoring")
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setContentIntent(pendingIntent)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && heartbeatRunnable != null) {
            handler.removeCallbacks(heartbeatRunnable);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getCurrentISO8601Timestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }
}
