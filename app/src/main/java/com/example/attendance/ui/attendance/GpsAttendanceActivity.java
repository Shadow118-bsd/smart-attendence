package com.example.attendance.ui.attendance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.databinding.ActivityGpsAttendanceBinding;

public class GpsAttendanceActivity extends AppCompatActivity {
    private ActivityGpsAttendanceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGpsAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
    }
}
