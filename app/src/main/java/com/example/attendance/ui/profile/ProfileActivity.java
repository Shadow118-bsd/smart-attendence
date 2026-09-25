package com.example.attendance.ui.profile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.databinding.ActivityProfileBinding;
import com.example.attendance.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SessionManager sessionManager = new SessionManager(this);
        binding.tvProfileName.setText("Họ và tên: " + sessionManager.getUserName());
        binding.tvProfileCode.setText("Mã nhân viên: " + sessionManager.getEmployeeId());
    }
}
