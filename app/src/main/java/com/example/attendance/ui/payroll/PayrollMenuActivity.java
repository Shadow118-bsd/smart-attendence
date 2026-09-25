package com.example.attendance.ui.payroll;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.databinding.ActivityPayrollMenuBinding;

public class PayrollMenuActivity extends AppCompatActivity {
    private ActivityPayrollMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayrollMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // 1. Bảng công
        binding.btnTimesheet.setOnClickListener(v -> {
            startActivity(new Intent(this, TimesheetActivity.class));
        });

        // 2. Phiếu lương
        binding.btnPayslip.setOnClickListener(v -> {
            startActivity(new Intent(this, PayslipActivity.class));
        });
    }
}
