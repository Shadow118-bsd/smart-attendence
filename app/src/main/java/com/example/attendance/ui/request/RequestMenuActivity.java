package com.example.attendance.ui.request;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.databinding.ActivityRequestMenuBinding;

public class RequestMenuActivity extends AppCompatActivity {
    private ActivityRequestMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // 1. Đăng ký nghỉ/công tác/đào tạo
        binding.btnLeaveRequest.setOnClickListener(v -> {
            startActivity(new Intent(this, LeaveRequestActivity.class));
        });

        // 2. Đăng ký làm thêm
        binding.btnOtRequest.setOnClickListener(v -> {
            startActivity(new Intent(this, OvertimeRequestActivity.class));
        });

        // 3. Đề xuất thôi việc
        binding.btnResignRequest.setOnClickListener(v -> {
            startActivity(new Intent(this, ResignRequestActivity.class));
        });

        // 4. Lịch sử đăng ký
        binding.btnRequestHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, RequestHistoryActivity.class));
        });
    }
}
