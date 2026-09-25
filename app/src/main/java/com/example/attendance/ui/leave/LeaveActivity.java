package com.example.attendance.ui.leave;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.databinding.ActivityLeaveBinding;

public class LeaveActivity extends AppCompatActivity {
    private ActivityLeaveBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSubmitLeave.setOnClickListener(v -> {
            String reason = binding.etLeaveReason.getText() != null ? binding.etLeaveReason.getText().toString() : "";
            if (reason.trim().isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập lý do xin nghỉ!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Đã gửi đơn xin nghỉ phép (Trạng thái: PENDING)", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
