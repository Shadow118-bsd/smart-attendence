package com.example.attendance.ui.attendance;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.attendance.data.repository.AttendanceRepository;
import com.example.attendance.databinding.ActivityInOutDataBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InOutDataActivity extends AppCompatActivity {
    private ActivityInOutDataBinding binding;
    private InOutRecordAdapter adapter;
    private AttendanceRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInOutDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new AttendanceRepository(this);
        adapter = new InOutRecordAdapter();

        binding.btnBack.setOnClickListener(v -> finish());

        // Setup Initial Dates
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(cal.getTime());
        binding.tvFromDate.setText(currentDate);
        binding.tvToDate.setText(currentDate);

        binding.tvFromDate.setOnClickListener(v -> showDatePicker(binding.tvFromDate));
        binding.tvToDate.setOnClickListener(v -> showDatePicker(binding.tvToDate));

        binding.rvInOutRecords.setLayoutManager(new LinearLayoutManager(this));
        binding.rvInOutRecords.setAdapter(adapter);

        loadLocalRecords();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocalRecords();
    }

    private void loadLocalRecords() {
        repository.getLocalAttendanceRecords(records -> {
            runOnUiThread(() -> {
                if (records != null) {
                    adapter.setList(records);
                }
            });
        });
    }

    private void showDatePicker(TextView targetTextView) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            targetTextView.setText(sdf.format(cal.getTime()));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
}
