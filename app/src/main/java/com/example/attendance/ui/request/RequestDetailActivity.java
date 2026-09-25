package com.example.attendance.ui.request;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.databinding.ActivityRequestDetailBinding;

public class RequestDetailActivity extends AppCompatActivity {
    private ActivityRequestDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnCancelRequest.setOnClickListener(v -> {
            Toast.makeText(this, "Đã gửi yêu cầu Hủy đơn!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
