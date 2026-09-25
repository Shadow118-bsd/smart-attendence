package com.example.attendance.ui.approval;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.databinding.ActivityApprovalMenuBinding;

public class ApprovalMenuActivity extends AppCompatActivity {
    private ActivityApprovalMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApprovalMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // 1. Phê duyệt nghỉ
        binding.btnApproveLeave.setOnClickListener(v -> openApprovalList("Phê duyệt nghỉ"));

        // 2. Phê duyệt làm thêm
        binding.btnApproveOt.setOnClickListener(v -> openApprovalList("Phê duyệt làm thêm"));

        // 3. Phê duyệt xác nhận công
        binding.btnApproveWorkConfirm.setOnClickListener(v -> openApprovalList("Phê duyệt giải trình công"));

        // 4. Phê duyệt HR Process
        binding.btnApproveHrProcess.setOnClickListener(v -> openApprovalList("Phê duyệt HR Process"));

        // 5. Lịch sử phê duyệt
        binding.btnApprovalHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, ApprovalHistoryActivity.class));
        });
    }

    private void openApprovalList(String title) {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra("TITLE", title);
        startActivity(intent);
    }
}
