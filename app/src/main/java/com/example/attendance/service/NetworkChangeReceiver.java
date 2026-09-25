package com.example.attendance.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.attendance.worker.SyncWorker;

public class NetworkChangeReceiver {
    private static final String TAG = "NetworkChangeReceiver";
    private static NetworkChangeReceiver instance;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean isRegistered = false;

    public static synchronized NetworkChangeReceiver getInstance() {
        if (instance == null) {
            instance = new NetworkChangeReceiver();
        }
        return instance;
    }

    public void register(@NonNull Context context) {
        if (isRegistered) return;
        Context appContext = context.getApplicationContext();
        connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return;

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Log.d(TAG, "Internet connection restored! Triggering instant SyncWorker.");
                triggerInstantSync(appContext);
            }
        };

        try {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
            isRegistered = true;
            Log.d(TAG, "NetworkCallback registered successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to register NetworkCallback: " + e.getMessage());
        }
    }

    public void triggerInstantSync(Context context) {
        try {
            OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(SyncWorker.class).build();
            WorkManager.getInstance(context).enqueueUniqueWork(
                    "InstantSyncWork",
                    ExistingWorkPolicy.REPLACE,
                    syncRequest
            );
        } catch (Exception e) {
            Log.e(TAG, "Failed to enqueue SyncWorker: " + e.getMessage());
        }
    }
}
