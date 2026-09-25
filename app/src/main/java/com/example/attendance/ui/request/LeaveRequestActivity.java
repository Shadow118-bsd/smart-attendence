package com.example.attendance.ui.request;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.attendance.data.model.request.LeaveRequest;
import com.example.attendance.databinding.ActivityLeaveRequestBinding;
import com.example.attendance.viewmodel.RequestViewModel;

public class LeaveRequestActivity extends AppCompatActivity {
    private ActivityLeaveRequestBinding binding;
    private RequestViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaveRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RequestViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());

        // Populate Leave Type Spinner
        String[] leaveTypes = new String[]{"[P] Nghỉ phép năm", "[CT] Công tác", "[DT] Đào tạo", "[RO] Nghỉ ốm"};
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, leaveTypes);
        binding.spLeaveType.setAdapter(adapterType);

        // Populate Reason Spinner
        String[] reasons = new String[]{"Chọn lý do", "Giải quyết việc cá nhân", "Bệnh", "Học tập", "Công tác tỉnh"};
        ArrayAdapter<String> adapterReason = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, reasons);
        binding.spReason.setAdapter(adapterReason);

        // Setup Date Pickers
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());
        binding.tvFromDate.setText(currentDate);
        binding.tvToDate.setText(currentDate);

        binding.btnFromDate.setOnClickListener(v -> showDatePicker(binding.tvFromDate));
        binding.btnToDate.setOnClickListener(v -> showDatePicker(binding.tvToDate));

        viewModel.getSubmitResult().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Tạo đơn nghỉ/công tác thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        binding.btnSubmitLeave.setOnClickListener(v -> {
            String fromStr = binding.tvFromDate.getText().toString();
            String toStr = binding.tvToDate.getText().toString();

            try {
                Date fromDate = sdf.parse(fromStr);
                Date toDate = sdf.parse(toStr);
                if (fromDate != null && toDate != null && fromDate.after(toDate)) {
                    Toast.makeText(this, "Lỗi: Ngày bắt đầu không thể sau ngày kết thúc!", Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (Exception ignored) {}

            LeaveRequest req = new LeaveRequest(
                    binding.spLeaveType.getSelectedItem().toString(),
                    fromStr,
                    toStr,
                    binding.cbDeclareDaily.isChecked(),
                    8.0,
                    binding.etAttachmentNote.getText().toString(),
                    binding.spReason.getSelectedItem().toString(),
                    binding.etReasonDetail.getText().toString()
            );
            viewModel.submitLeave(req);
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
