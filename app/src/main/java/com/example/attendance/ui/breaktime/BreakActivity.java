package com.example.attendance.ui.breaktime;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.databinding.ActivityBreakBinding;

public class BreakActivity extends AppCompatActivity {
    private ActivityBreakBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBreakBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý nghỉ trưa");
        }

        binding.btnStartBreak.setOnClickListener(v -> {
            Toast.makeText(this, "Đã gửi yêu cầu Bắt đầu nghỉ trưa!", Toast.LENGTH_SHORT).show();
            finish();
        });

        binding.btnEndBreak.setOnClickListener(v -> {
            Toast.makeText(this, "Đã gửi yêu cầu Kết thúc nghỉ trưa!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
