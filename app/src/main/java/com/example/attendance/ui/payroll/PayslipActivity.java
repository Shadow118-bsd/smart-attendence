package com.example.attendance.ui.payroll;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.attendance.databinding.ActivityPayslipBinding;
import com.example.attendance.viewmodel.PayrollViewModel;

import java.util.Calendar;
import java.util.Locale;

public class PayslipActivity extends AppCompatActivity {
    private ActivityPayslipBinding binding;
    private PayrollViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayslipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PayrollViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());

        // Dynamic Years
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonthZeroBased = now.get(Calendar.MONTH); // 0-11

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
                fetchPayslipData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        binding.spYear.setOnItemSelectedListener(spinnerListener);
        binding.spMonth.setOnItemSelectedListener(spinnerListener);

        viewModel.getPayslipLiveData().observe(this, data -> {
            if (data != null) {
                binding.tvEmployeeId.setText(data.getEmployeeId() != null ? data.getEmployeeId() : "--");
                binding.tvFullName.setText(data.getFullName() != null ? data.getFullName() : "--");
                binding.tvDepartment.setText(data.getDepartment() != null ? data.getDepartment() : "--");
                binding.tvJobTitle.setText(data.getJobTitle() != null ? data.getJobTitle() : "--");
                binding.tvTaxCode.setText(data.getTaxCode() != null ? data.getTaxCode() : "--");
                binding.tvDependentsCount.setText(String.valueOf(data.getDependentsCount()));
                binding.tvBaseSalary.setText(data.getBaseSalary() != null ? data.getBaseSalary() : "--");
                binding.tvTkqcvIncome.setText(data.getTkqcvIncome() != null ? data.getTkqcvIncome() : "--");
                binding.tvPaRatio.setText(data.getPaRatio() != null ? data.getPaRatio() : "--");
                binding.tvStandardWorkHours.setText(String.valueOf(data.getStandardWorkHours()));
                binding.tvActualWorkHours.setText(String.valueOf(data.getActualWorkHours()));
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                android.widget.Toast.makeText(this, error, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPayslipData() {
        String selectedYear = binding.spYear.getSelectedItem() != null ? binding.spYear.getSelectedItem().toString() : "2026";
        int monthIndex = binding.spMonth.getSelectedItemPosition() + 1; // 1-12
        String selectedMonth = String.format(Locale.getDefault(), "%02d", monthIndex);
        viewModel.loadPayslip(selectedYear, selectedMonth);
    }
}

