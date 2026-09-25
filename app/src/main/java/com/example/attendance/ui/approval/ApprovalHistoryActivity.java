package com.example.attendance.ui.approval;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.attendance.databinding.ActivityApprovalHistoryBinding;
import com.example.attendance.viewmodel.ApprovalViewModel;

import java.util.Calendar;
import java.util.Locale;

public class ApprovalHistoryActivity extends AppCompatActivity {
    private ActivityApprovalHistoryBinding binding;
    private ApprovalHistoryAdapter adapter;
    private ApprovalViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApprovalHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ApprovalViewModel.class);

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
                fetchApprovalHistory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        binding.spYear.setOnItemSelectedListener(spinnerListener);
        binding.spMonth.setOnItemSelectedListener(spinnerListener);

        adapter = new ApprovalHistoryAdapter();
        binding.rvApprovalHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvApprovalHistory.setAdapter(adapter);

        viewModel.getApprovalHistoryList().observe(this, list -> {
            if (list != null) {
                adapter.setList(list);
            }
        });
    }

    private void fetchApprovalHistory() {
        String selectedYear = binding.spYear.getSelectedItem() != null ? binding.spYear.getSelectedItem().toString() : String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        int monthIndex = binding.spMonth.getSelectedItemPosition() + 1; // 1-12
        String selectedMonth = String.format(Locale.getDefault(), "%02d", monthIndex);
        viewModel.loadApprovalHistory(selectedYear, selectedMonth);
    }
}
