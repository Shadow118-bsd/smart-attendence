package com.example.attendance.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.attendance.databinding.FragmentHomeBinding;
import com.example.attendance.ui.attendance.FaceVerificationActivity;
import com.example.attendance.ui.attendance.GpsAttendanceActivity;
import com.example.attendance.ui.attendance.InOutDataActivity;
import com.example.attendance.ui.payroll.PayrollMenuActivity;
import com.example.attendance.ui.request.RequestMenuActivity;
import com.example.attendance.ui.approval.ApprovalMenuActivity;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Dữ liệu In/Out
        binding.btnGridInOut.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), InOutDataActivity.class));
        });

        // 2. Đăng ký
        binding.btnGridRequest.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), RequestMenuActivity.class));
        });

        // 3. Phê duyệt
        binding.btnGridApproval.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ApprovalMenuActivity.class));
        });

        // 4. Bảng công lương
        binding.btnGridPayroll.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), PayrollMenuActivity.class));
        });

        // 5. Tin tức
        binding.btnGridNews.setOnClickListener(v -> {
            // News Activity or Web view
        });

        // 6. Khảo sát
        binding.btnGridSurvey.setOnClickListener(v -> {
            // Survey Activity
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTodayShiftData();
    }

    private void loadTodayShiftData() {
        if (binding == null) return;
        new Thread(() -> {
            String todayDate = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
            com.example.attendance.data.local.AppDatabase db = com.example.attendance.data.local.AppDatabase.getInstance(requireContext());
            java.util.List<com.example.attendance.data.local.entity.AttendanceRecordEntity> records = db.attendanceRecordDao().getRecordsByDate(todayDate);

            String checkInTime = "--:--";
            String checkOutTime = "--:--";
            boolean isCheckedIn = false;
            boolean isCheckedOut = false;
            boolean isTempOut = false;
            String tempOutReason = "";

            if (records != null) {
                for (com.example.attendance.data.local.entity.AttendanceRecordEntity rec : records) {
                    if ("CHECK_IN".equals(rec.getEventType())) {
                        checkInTime = rec.getTimeString();
                        isCheckedIn = true;
                    } else if ("CHECK_OUT".equals(rec.getEventType())) {
                        checkOutTime = rec.getTimeString();
                        isCheckedOut = true;
                    } else if ("TEMP_OUT".equals(rec.getEventType())) {
                        isTempOut = true;
                    } else if ("TEMP_IN".equals(rec.getEventType())) {
                        isTempOut = false;
                    }
                }
            }

            final String finalCheckIn = checkInTime;
            final String finalCheckOut = checkOutTime;
            final boolean finalCheckedIn = isCheckedIn;
            final boolean finalCheckedOut = isCheckedOut;
            final boolean finalIsTempOut = isTempOut;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    binding.tvCheckInTime.setText(finalCheckIn);
                    binding.tvCheckOutTime.setText(finalCheckOut);

                    if (!finalCheckedIn) {
                        binding.tvPresenceBadge.setText("⚪ Chưa vào ca");
                        binding.tvWorkingDuration.setText("⏱️ Chưa bắt đầu ca làm việc");
                        binding.btnSmartAttendance.setText("🟢 VÀO CA LÀM VIỆC (CHECK-IN)");
                        binding.btnSmartAttendance.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), com.example.attendance.R.color.success));
                        binding.btnSmartAttendance.setOnClickListener(v -> launchAttendance("CHECK_IN"));
                        binding.btnTempOut.setVisibility(View.GONE);
                    } else if (!finalCheckedOut) {
                        binding.btnTempOut.setVisibility(View.VISIBLE);
                        if (finalIsTempOut) {
                            binding.tvPresenceBadge.setText("🟡 Ra ngoài tạm thời");
                            binding.tvWorkingDuration.setText("⏱️ Đang ra ngoài tạm thời (Báo việc gấp)");
                            binding.btnTempOut.setText("🔵 QUAY LẠI LÀM VIỆC");
                            binding.btnTempOut.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), com.example.attendance.R.color.primary));
                            binding.btnTempOut.setOnClickListener(v -> launchAttendance("TEMP_IN"));
                        } else {
                            binding.tvPresenceBadge.setText("🟢 Trong vùng Wi-Fi");
                            binding.tvWorkingDuration.setText("⏱️ Đang trong ca làm việc • Đã ghi nhận hiện diện");
                            binding.btnTempOut.setText("🚶 RA NGOÀI TẠM THỜI");
                            binding.btnTempOut.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), com.example.attendance.R.color.accent));
                            binding.btnTempOut.setOnClickListener(v -> showTemporaryOutBottomSheet());
                        }

                        binding.btnSmartAttendance.setText("🟧 RA CA LÀM VIỆC (CHECK-OUT)");
                        binding.btnSmartAttendance.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), com.example.attendance.R.color.warning));
                        binding.btnSmartAttendance.setOnClickListener(v -> launchAttendance("CHECK_OUT"));
                    } else {
                        binding.tvPresenceBadge.setText("🔘 Đã kết thúc ca");
                        binding.tvWorkingDuration.setText("⏱️ Đã hoàn thành ca làm việc hôm nay");
                        binding.btnSmartAttendance.setText("🔘 ĐÃ HOÀN THÀNH CA LÀM VIỆC");
                        binding.btnSmartAttendance.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), com.example.attendance.R.color.border_gray));
                        binding.btnSmartAttendance.setOnClickListener(v -> {
                            android.widget.Toast.makeText(requireContext(), "Bạn đã hoàn thành Check-in & Check-out trong ngày!", android.widget.Toast.LENGTH_SHORT).show();
                        });
                        binding.btnTempOut.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    private void showTemporaryOutBottomSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(com.example.attendance.R.layout.dialog_temporary_out, null);
        dialog.setContentView(dialogView);

        android.widget.EditText edtReason = dialogView.findViewById(com.example.attendance.R.id.edtTempOutReason);
        android.widget.Button btnConfirm = dialogView.findViewById(com.example.attendance.R.id.btnConfirmTempOut);
        android.widget.Button btnCancel = dialogView.findViewById(com.example.attendance.R.id.btnCancelTempOut);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            String reason = edtReason.getText().toString().trim();
            if (reason.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Vui lòng nhập lý do việc gấp!", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();
            Intent intent = new Intent(requireContext(), FaceVerificationActivity.class);
            intent.putExtra("ACTION", "TEMP_OUT");
            intent.putExtra("REASON", reason);
            startActivity(intent);
        });

        dialog.show();
    }

    private void launchAttendance(String action) {
        Intent intent = new Intent(requireContext(), FaceVerificationActivity.class);
        intent.putExtra("ACTION", action);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
