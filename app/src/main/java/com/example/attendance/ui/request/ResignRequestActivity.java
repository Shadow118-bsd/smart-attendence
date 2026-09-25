package com.example.attendance.ui.request;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.attendance.data.model.request.ResignRequest;
import com.example.attendance.databinding.ActivityResignRequestBinding;
import com.example.attendance.viewmodel.RequestViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ResignRequestActivity extends AppCompatActivity {
    private ActivityResignRequestBinding binding;
    private RequestViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResignRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RequestViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());

        // Setup Initial Dates
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());
        binding.tvEffectiveDate.setText(currentDate);
        binding.tvLastWorkingDate.setText(currentDate);

        binding.btnEffectiveDate.setOnClickListener(v -> showDatePicker(binding.tvEffectiveDate));
        binding.btnLastWorkingDate.setOnClickListener(v -> showDatePicker(binding.tvLastWorkingDate));

        // Resign Reasons Spinner
        String[] reasons = new String[]{"Người lao động viết đơn xin nghỉ", "Nghỉ hưu", "Thỏa thuận chấm dứt HĐLĐ", "Lý do cá nhân"};
        ArrayAdapter<String> adapterReason = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, reasons);
        binding.spResignReason.setAdapter(adapterReason);

        viewModel.getSubmitResult().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Gửi đề xuất thôi việc thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        binding.btnSubmitResign.setOnClickListener(v -> {
            if (!binding.cbCommitment.isChecked()) {
                Toast.makeText(this, "Vui lòng tích chọn cam kết bàn giao công việc", Toast.LENGTH_SHORT).show();
                return;
            }
            ResignRequest req = new ResignRequest(
                    binding.spResignReason.getSelectedItem().toString(),
                    binding.etResignReasonDetail.getText().toString(),
                    binding.tvEffectiveDate.getText().toString(),
                    binding.tvLastWorkingDate.getText().toString(),
                    binding.etOtherContent.getText().toString(),
                    binding.cbCommitment.isChecked()
            );
            viewModel.submitResign(req);
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
