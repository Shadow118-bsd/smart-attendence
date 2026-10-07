package com.example.attendance.ui.approval;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.attendance.R;
import com.example.attendance.data.model.response.ApprovalHistoryItem;
import com.example.attendance.databinding.ActivityApprovalDetailBinding;
import com.example.attendance.utils.SessionManager;
import com.example.attendance.viewmodel.ApprovalViewModel;

public class ApprovalDetailActivity extends AppCompatActivity {
    private ActivityApprovalDetailBinding binding;
    private ApprovalViewModel viewModel;
    private SessionManager sessionManager;
    private String approvalId = "";
    private boolean isPending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApprovalDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ApprovalViewModel.class);
        sessionManager = new SessionManager(this);

        approvalId = getIntent().getStringExtra("APPROVAL_ID");
        isPending = getIntent().getBooleanExtra("IS_PENDING", false);

        binding.btnBack.setOnClickListener(v -> finish());

        setupActions();
        setupObservers();

        if (approvalId != null && !approvalId.isEmpty()) {
            viewModel.loadApprovalDetail(approvalId);
        }
    }

    private void setupObservers() {
        viewModel.getApprovalDetail().observe(this, item -> {
            if (item != null) {
                bindData(item);
            }
        });

        viewModel.getActionSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Xử lý phê duyệt thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindData(ApprovalHistoryItem item) {
        binding.tvApplicantName.setText(item.getEmployeeName() != null ? item.getEmployeeName() : "--");
        binding.tvRequestType.setText(item.getRequestType() != null ? item.getRequestType() : "--");
        binding.tvRequestDate.setText(item.getRequestDate() != null ? item.getRequestDate() : "--");
        binding.tvOtShift.setText(item.getOtShift() != null && !item.getOtShift().isEmpty() ? item.getOtShift() : "Cả ngày");
        binding.tvReason.setText(item.getReason() != null ? item.getReason() : "--");
        binding.tvReasonDetail.setText(item.getDetailReason() != null ? item.getDetailReason() : "--");

        boolean pending = isPending || "Chờ duyệt".equalsIgnoreCase(item.getStatus());

        if (pending) {
            binding.layoutActionButtons.setVisibility(View.VISIBLE);
            binding.cardApprovalInfo.setVisibility(View.GONE);
        } else {
            binding.layoutActionButtons.setVisibility(View.GONE);
            binding.cardApprovalInfo.setVisibility(View.VISIBLE);

            binding.tvApprovalStatus.setText(item.getStatus() != null ? item.getStatus() : "Đã duyệt");
            if ("Từ chối".equalsIgnoreCase(item.getStatus())) {
                binding.tvApprovalStatus.setTextColor(ContextCompat.getColor(this, R.color.danger));
            } else {
                binding.tvApprovalStatus.setTextColor(ContextCompat.getColor(this, R.color.success));
            }

            binding.tvApprovalDate.setText(item.getApprovalDate() != null && !item.getApprovalDate().isEmpty() ? item.getApprovalDate() : "--");
            binding.tvApproverName.setText(item.getApproverName() != null && !item.getApproverName().isEmpty() ? item.getApproverName() : "Quản lý bộ phận");
            binding.tvApproverRole.setText(item.getApproverRole() != null && !item.getApproverRole().isEmpty() ? item.getApproverRole() : "Người phê duyệt");
        }
    }

    private void setupActions() {
        // Nút Phê duyệt -> Hiện AlertDialog xác nhận (Chương 3)
        binding.btnApprove.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận phê duyệt")
                    .setMessage("Bạn có chắc chắn muốn phê duyệt đơn này không?")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Phê duyệt", (dialog, which) -> {
                        String approver = sessionManager.getUserName();
                        viewModel.approveRequest(approvalId, approver);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        // Nút Từ chối -> Hiện AlertDialog nhập lý do từ chối (Chương 3)
        binding.btnReject.setOnClickListener(v -> {
            final EditText input = new EditText(this);
            input.setHint("Nhập lý do từ chối (ví dụ: Không có người thay ca)...");
            input.setPadding(40, 20, 40, 20);

            FrameLayout container = new FrameLayout(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            params.leftMargin = 40;
            params.rightMargin = 40;
            input.setLayoutParams(params);
            container.addView(input);

            new AlertDialog.Builder(this)
                    .setTitle("Từ chối đơn")
                    .setMessage("Vui lòng ghi rõ lý do từ chối để nhân viên nắm được:")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setView(container)
                    .setPositiveButton("Từ chối đơn", (dialog, which) -> {
                        String reason = input.getText().toString().trim();
                        if (reason.isEmpty()) {
                            reason = "Không đáp ứng điều kiện";
                        }
                        String approver = sessionManager.getUserName();
                        viewModel.rejectRequest(approvalId, approver, reason);
                    })
                    .setNegativeButton("Hủy bỏ", null)
                    .show();
        });
    }
}
