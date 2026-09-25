package com.example.attendance;

import android.app.Application;
import android.util.Log;

import com.example.attendance.service.NetworkChangeReceiver;

public class AttendanceApplication extends Application {
    private static final String TAG = "AttendanceApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "AttendanceApplication started.");
        NetworkChangeReceiver.getInstance().register(this);
    }
}

