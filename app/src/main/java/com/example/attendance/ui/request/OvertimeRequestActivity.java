package com.example.attendance.ui.request;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.attendance.data.model.request.OTRequest;
import com.example.attendance.databinding.ActivityOvertimeRequestBinding;
import com.example.attendance.viewmodel.RequestViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class OvertimeRequestActivity extends AppCompatActivity {
    private ActivityOvertimeRequestBinding binding;
    private RequestViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOvertimeRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RequestViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());

        // Setup Initial Date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        binding.tvOtDate.setText(sdf.format(calendar.getTime()));

        binding.btnOtDate.setOnClickListener(v -> showDatePicker(binding.tvOtDate));
        binding.btnStartTime.setOnClickListener(v -> showTimePicker(binding.tvStartTime));
        binding.btnEndTime.setOnClickListener(v -> showTimePicker(binding.tvEndTime));

        // Reason Spinner
        String[] reasons = new String[]{"Chọn lý do", "Trực lễ", "Làm thêm giờ ngày lễ", "Tăng ca tiến độ dự án"};
        ArrayAdapter<String> adapterReason = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, reasons);
        binding.spOtReason.setAdapter(adapterReason);

        viewModel.getSubmitResult().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Tạo đơn làm thêm (OT) thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        binding.btnSubmitOt.setOnClickListener(v -> {
            OTRequest req = new OTRequest(
                    binding.tvEmployeeName.getText().toString(),
                    binding.tvOtDate.getText().toString(),
                    binding.etShiftName.getText().toString(),
                    binding.tvStartTime.getText().toString(),
                    binding.cbStartNextDay.isChecked(),
                    binding.tvEndTime.getText().toString(),
                    binding.cbEndNextDay.isChecked(),
                    binding.spOtReason.getSelectedItem().toString(),
                    binding.etOtReasonDetail.getText().toString()
            );
            viewModel.submitOT(req);
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

    private void showTimePicker(TextView targetTextView) {
        Calendar cal = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            targetTextView.setText(timeStr);
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        dialog.show();
    }
}
