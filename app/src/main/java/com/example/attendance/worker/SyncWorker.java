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
import com.example.attendance.data.model.request.CheckInRequest;
import com.example.attendance.data.model.request.CheckOutRequest;
import com.example.attendance.data.model.request.HeartbeatRequest;
import com.example.attendance.data.model.response.CheckInResponse;
import com.example.attendance.data.model.response.CheckOutResponse;
import com.example.attendance.data.model.response.HeartbeatResponse;
import com.example.attendance.service.WifiService;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Response;

public class SyncWorker extends Worker {
    private static final String TAG = "SyncWorker";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        WifiService wifiService = new WifiService(context);

        if (!wifiService.isInternetReachable()) {
            Log.d(TAG, "No internet connectivity for SyncWorker, retrying later.");
            return Result.retry();
        }

        AppDatabase db = AppDatabase.getInstance(context);
        List<PendingEventEntity> pendingEvents = db.pendingEventDao().getPendingEvents();

        if (pendingEvents == null || pendingEvents.isEmpty()) {
            Log.d(TAG, "No pending events to sync.");
            return Result.success();
        }

        Gson gson = new Gson();
        AttendanceApi api = RetrofitClient.getClient(context).create(AttendanceApi.class);

        for (PendingEventEntity event : pendingEvents) {
            event.setStatus("SYNCING");
            db.pendingEventDao().update(event);

            try {
                boolean success = false;
                if ("CHECK_IN".equals(event.getEventType())) {
                    CheckInRequest req = gson.fromJson(event.getPayloadJson(), CheckInRequest.class);
                    Response<CheckInResponse> res = api.checkIn(req).execute();
                    success = res.isSuccessful();
                } else if ("CHECK_OUT".equals(event.getEventType())) {
                    CheckOutRequest req = gson.fromJson(event.getPayloadJson(), CheckOutRequest.class);
                    Response<CheckOutResponse> res = api.checkOut(req).execute();
                    success = res.isSuccessful();
                } else if ("HEARTBEAT".equals(event.getEventType())) {
                    HeartbeatRequest req = gson.fromJson(event.getPayloadJson(), HeartbeatRequest.class);
                    Response<HeartbeatResponse> res = api.heartbeat(req).execute();
                    success = res.isSuccessful();
                }

                if (success) {
                    event.setStatus("SYNCED");
                    db.pendingEventDao().update(event);
                    Log.d(TAG, "Successfully synced event ID: " + event.getEventId());
                } else {
                    event.setStatus("PENDING");
                    event.setRetryCount(event.getRetryCount() + 1);
                    db.pendingEventDao().update(event);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed syncing event ID: " + event.getEventId() + ", error: " + e.getMessage());
                event.setStatus("PENDING");
                event.setRetryCount(event.getRetryCount() + 1);
                db.pendingEventDao().update(event);
            }
        }

        db.pendingEventDao().deleteSyncedEvents();
        return Result.success();
    }
}
