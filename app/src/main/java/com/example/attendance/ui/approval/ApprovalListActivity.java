package com.example.attendance.ui.approval;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.databinding.ActivityApprovalListBinding;

public class ApprovalListActivity extends AppCompatActivity {
    private ActivityApprovalListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApprovalListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String title = getIntent().getStringExtra("TITLE");
        if (title != null && !title.isEmpty()) {
            binding.tvApprovalTitle.setText(title);
        }

        binding.btnBack.setOnClickListener(v -> finish());
    }
}
