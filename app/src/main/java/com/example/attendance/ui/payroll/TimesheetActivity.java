package com.example.attendance.ui.payroll;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.attendance.databinding.ActivityTimesheetBinding;
import com.example.attendance.viewmodel.PayrollViewModel;

import java.util.Calendar;
import java.util.Locale;

public class TimesheetActivity extends AppCompatActivity {
    private ActivityTimesheetBinding binding;
    private PayrollViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimesheetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PayrollViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());

        // Dynamic Years
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonthZeroBased = now.get(Calendar.MONTH);

        String[] years = new String[]{
                String.valueOf(currentYear),
                String.valueOf(currentYear - 1),
                String.valueOf(currentYear - 2)
        };
        ArrayAdapter<String> adapterYear = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        binding.spYear.setAdapter(adapterYear);

        // Full 12 Months
        String[] months = new String[]{
                "Tháng 01", "Tháng 02", "Tháng 03", "Tháng 04", "Tháng 05", "Tháng 06",
                "Tháng 07", "Tháng 08", "Tháng 09", "Tháng 10", "Tháng 11", "Tháng 12"
        };
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        binding.spMonth.setAdapter(adapterMonth);
        binding.spMonth.setSelection(currentMonthZeroBased);

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchTimesheetData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        binding.spYear.setOnItemSelectedListener(spinnerListener);
        binding.spMonth.setOnItemSelectedListener(spinnerListener);

        viewModel.getTimesheetLiveData().observe(this, data -> {
            if (data != null) {
                binding.tvShiftName.setText(data.getShiftName() != null ? data.getShiftName() : "--");
                binding.tvCheckTime.setText(data.getCheckTime() != null ? data.getCheckTime() : "--");
                binding.tvOvertimeStandard.setText(data.getOvertimeStandard() != null ? data.getOvertimeStandard() : "--");
                binding.tvStandardWorkHours.setText(String.valueOf(data.getStandardWorkHours()));
                binding.tvStartAnnualLeave.setText(String.valueOf(data.getStartAnnualLeave()));
                binding.tvActualWorkHours.setText(String.valueOf(data.getActualWorkHours()));
                binding.tvLeaveHours.setText(String.valueOf(data.getLeaveHours()));
                binding.tvPolicyLeaveHours.setText(String.valueOf(data.getPolicyLeaveHours()));
                binding.tvBusinessTripHours.setText(String.valueOf(data.getBusinessTripHours()));
                binding.tvStopWorkHours.setText(String.valueOf(data.getStopWorkHours()));
                binding.tvPaidLeaveHours.setText(String.valueOf(data.getPaidLeaveHours()));
                binding.tvTotalPaidHours.setText(String.valueOf(data.getTotalPaidHours()));
                binding.tvTotalTnHours.setText(String.valueOf(data.getTotalTnTkqcvHours()));
                binding.tvUnpaidLeaveHours.setText(String.valueOf(data.getUnpaidLeaveHours()));
                binding.tvMealCount.setText(String.valueOf(data.getMealCount()));
                binding.tvNightShiftHours.setText(String.valueOf(data.getNightShiftHours()));
                binding.tvEndAnnualLeave.setText(String.valueOf(data.getEndAnnualLeave()));
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                android.widget.Toast.makeText(this, error, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTimesheetData() {
        String selectedYear = binding.spYear.getSelectedItem() != null ? binding.spYear.getSelectedItem().toString() : "2026";
        int monthIndex = binding.spMonth.getSelectedItemPosition() + 1; // 1-12
        String selectedMonth = String.format(Locale.getDefault(), "%02d", monthIndex);
        viewModel.loadTimesheet(selectedYear, selectedMonth);
    }
}

