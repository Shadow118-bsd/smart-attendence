package com.example.attendance.ui.history;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.databinding.ActivityAttendanceHistoryBinding;

public class AttendanceHistoryActivity extends AppCompatActivity {
    private ActivityAttendanceHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
