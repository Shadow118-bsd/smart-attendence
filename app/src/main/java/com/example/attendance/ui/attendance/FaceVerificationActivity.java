package com.example.attendance.ui.attendance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.example.attendance.MainActivity;
import com.example.attendance.databinding.ActivityFaceVerificationBinding;
import com.example.attendance.service.FaceService;
import com.example.attendance.utils.AttendanceVerifier;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

public class FaceVerificationActivity extends AppCompatActivity {
    private ActivityFaceVerificationBinding binding;
    private ImageCapture imageCapture;
    private FaceService faceService;
    private int lensFacing = CameraSelector.LENS_FACING_FRONT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFaceVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        faceService = new FaceService(this);

        // Nút Back quay lại MainActivity
        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(FaceVerificationActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        startCamera();

        binding.btnSwitchCamera.setOnClickListener(v -> switchCamera());
        binding.btnCaptureFace.setOnClickListener(v -> captureAndVerifyFace());
    }

    private void switchCamera() {
        lensFacing = (lensFacing == CameraSelector.LENS_FACING_FRONT) ?
                CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT;
        startCamera();
        Toast.makeText(this, "Đã chuyển sang Camera " + (lensFacing == CameraSelector.LENS_FACING_FRONT ? "Trước" : "Sau"), Toast.LENGTH_SHORT).show();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(binding.viewFinder.getDisplay().getRotation())
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Không thể khởi tạo Camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void captureAndVerifyFace() {
        if (imageCapture == null) return;

        String action = getIntent().getStringExtra("ACTION");
        if (action == null || action.isEmpty()) action = "CHECK_IN";
        final String finalAction = action;

        com.example.attendance.service.LocationService locationService = new com.example.attendance.service.LocationService(this);
        com.example.attendance.service.WifiService wifiService = new com.example.attendance.service.WifiService(this);

        com.example.attendance.data.model.request.WifiEvidence wifiEvidence = wifiService.collectWifiEvidence();

        if (!"TEMP_OUT".equals(finalAction) && (wifiEvidence == null || !wifiEvidence.isConnected())) {
            showErrorBottomSheet(
                    AttendanceVerifier.ErrorType.WIFI_FAILED,
                    "Chưa kết loại mạng Wi-Fi hợp lệ của công ty (FPT_Office_5G).",
                    "Vui lòng bật Wi-Fi và kết nối đúng mạng Wi-Fi công ty trước khi điểm danh.",
                    () -> captureAndVerifyFace()
            );
            return;
        }

        binding.progressBarFace.setVisibility(View.VISIBLE);
        binding.btnCaptureFace.setEnabled(false);

        File photoDir = new File(getFilesDir(), "face_captures");
        if (!photoDir.exists()) photoDir.mkdirs();
        File photoFile = new File(photoDir, "face_" + System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                String photoPath = photoFile.getAbsolutePath();
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);

                faceService.analyzeFaceBitmap(bitmap, faceResult -> {
                    locationService.getCurrentLocation(locationEvidence -> runOnUiThread(() -> {
                        binding.progressBarFace.setVisibility(View.GONE);
                        binding.btnCaptureFace.setEnabled(true);

                        boolean faceVerified = faceResult.getStatus() == FaceService.FaceVerificationStatus.FACE_VERIFIED;
                        com.example.attendance.data.model.request.FaceEvidence faceEvidence =
                                new com.example.attendance.data.model.request.FaceEvidence(faceVerified, faceResult.isLivenessPassed());

                        if ("TEMP_OUT".equals(finalAction)) {
                            if (faceVerified) {
                                String reason = getIntent().getStringExtra("REASON");
                                executeTemporaryOut(reason, faceEvidence, locationEvidence);
                            } else {
                                if (photoFile.exists()) photoFile.delete();
                                showErrorBottomSheet(AttendanceVerifier.ErrorType.FACE_FAILED, "Khuôn mặt không khớp dữ liệu sinh trắc học.", "Vui lòng nhìn thẳng vào camera và chụp lại.", () -> captureAndVerifyFace());
                            }
                        } else {
                            AttendanceVerifier.VerificationResult verification =
                                    AttendanceVerifier.verify(faceEvidence, locationEvidence, wifiEvidence);

                            if (verification.isFullyValid()) {
                                executeDirectAttendance(finalAction, faceEvidence, locationEvidence, wifiEvidence, photoPath);
                            } else {
                                if (photoFile.exists()) photoFile.delete();

                                String reason;
                                String guidance;
                                switch (verification.getErrorType()) {
                                    case FACE_FAILED:
                                        reason = "Khuôn mặt không khớp với dữ liệu sinh trắc học đã đăng ký.";
                                        guidance = "Vui lòng nhìn thẳng vào ống kính camera, tháo bớt khẩu trang/kính râm và đảm bảo đủ ánh sáng.";
                                        break;
                                    case LIVENESS_FAILED:
                                        reason = "Không vượt qua kiểm tra liveness (phát hiện mắt nhắm hoặc ảnh tĩnh).";
                                        guidance = "Vui lòng nháy mắt tự nhiên, giữ đầu thẳng và không chụp lại ảnh qua màn hình/ảnh in.";
                                        break;
                                    case GPS_MOCK_DETECTED:
                                        reason = "Hệ thống phát hiện vị trí GPS giả lập (Fake GPS).";
                                        guidance = "Vui lòng tắt phần mềm giả lập vị trí và sử dụng GPS thực tế của thiết bị.";
                                        break;
                                    case GPS_FAILED:
                                        reason = "Khoảng cách hiện tại (" + Math.round(verification.getDistanceMeters()) + "m) vượt quá bán kính 1000m của công ty.";
                                        guidance = "Vui lòng di chuyển vào khu vực văn phòng công ty và kiểm tra lại vị trí GPS.";
                                        break;
                                    case WIFI_FAILED:
                                    default:
                                        reason = "Kết nối Wi-Fi không đạt tiêu chuẩn văn phòng.";
                                        guidance = "Vui lòng kết nối vào Wi-Fi hợp lệ của công ty.";
                                        break;
                                }

                                showErrorBottomSheet(verification.getErrorType(), reason, guidance, () -> captureAndVerifyFace());
                            }
                        }
                    }));
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                binding.progressBarFace.setVisibility(View.GONE);
                binding.btnCaptureFace.setEnabled(true);
                Toast.makeText(FaceVerificationActivity.this, "Lỗi chụp ảnh: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showErrorBottomSheet(AttendanceVerifier.ErrorType errorType, String reason, String guidance, Runnable primaryAction) {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(com.example.attendance.R.layout.dialog_attendance_error, null);
        dialog.setContentView(dialogView);

        android.widget.TextView tvReason = dialogView.findViewById(com.example.attendance.R.id.tvErrorReason);
        android.widget.TextView tvGuidance = dialogView.findViewById(com.example.attendance.R.id.tvErrorGuidance);
        android.widget.Button btnPrimary = dialogView.findViewById(com.example.attendance.R.id.btnPrimaryAction);
        android.widget.Button btnSecondary = dialogView.findViewById(com.example.attendance.R.id.btnSecondaryAction);

        tvReason.setText(reason);
        tvGuidance.setText(guidance);

        if (errorType == AttendanceVerifier.ErrorType.WIFI_FAILED) {
            btnSecondary.setText("Cài đặt Wi-Fi");
            btnSecondary.setOnClickListener(v -> {
                dialog.dismiss();
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
            });
        } else if (errorType == AttendanceVerifier.ErrorType.GPS_FAILED) {
            btnSecondary.setText("Cài đặt GPS");
            btnSecondary.setOnClickListener(v -> {
                dialog.dismiss();
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            });
        } else {
            btnSecondary.setText("Đóng");
            btnSecondary.setOnClickListener(v -> dialog.dismiss());
        }

        btnPrimary.setText("Thử lại");
        btnPrimary.setOnClickListener(v -> {
            dialog.dismiss();
            if (primaryAction != null) {
                primaryAction.run();
            }
        });

        dialog.show();
    }

    private void executeTemporaryOut(String reason, com.example.attendance.data.model.request.FaceEvidence faceEvidence, com.example.attendance.data.model.request.LocationEvidence locationEvidence) {
        com.example.attendance.data.repository.AttendanceRepository repository = new com.example.attendance.data.repository.AttendanceRepository(this);
        repository.temporaryOut(reason, faceEvidence, locationEvidence, new com.example.attendance.data.repository.AttendanceRepository.AttendanceCallback<Void>() {
            @Override public void onSuccess(Void result) { showTicketBottomSheet("TEMP_OUT", null); }
            @Override public void onError(String message) { showTicketBottomSheet("TEMP_OUT", null); }
            @Override public void onSavedOffline(String eventId) { showTicketBottomSheet("TEMP_OUT", null); }
        });
    }

    private void executeDirectAttendance(String action, com.example.attendance.data.model.request.FaceEvidence faceEvidence, com.example.attendance.data.model.request.LocationEvidence locationEvidence, com.example.attendance.data.model.request.WifiEvidence wifiEvidence, String photoPath) {
        com.example.attendance.data.repository.AttendanceRepository repository = new com.example.attendance.data.repository.AttendanceRepository(this);

        if ("TEMP_IN".equals(action)) {
            repository.temporaryIn(faceEvidence, locationEvidence, new com.example.attendance.data.repository.AttendanceRepository.AttendanceCallback<Void>() {
                @Override public void onSuccess(Void result) { showTicketBottomSheet("TEMP_IN", wifiEvidence); }
                @Override public void onError(String message) { showTicketBottomSheet("TEMP_IN", wifiEvidence); }
                @Override public void onSavedOffline(String eventId) { showTicketBottomSheet("TEMP_IN", wifiEvidence); }
            });
        } else if ("CHECK_IN".equals(action)) {
            repository.checkIn(faceEvidence, locationEvidence, photoPath, new com.example.attendance.data.repository.AttendanceRepository.AttendanceCallback<com.example.attendance.data.model.response.CheckInResponse>() {
                @Override
                public void onSuccess(com.example.attendance.data.model.response.CheckInResponse res) {
                    showTicketBottomSheet(action, wifiEvidence);
                }
                @Override
                public void onError(String message) {
                    showTicketBottomSheet(action, wifiEvidence);
                }
                @Override
                public void onSavedOffline(String eventId) {
                    showTicketBottomSheet(action, wifiEvidence);
                }
            });
        } else {
            repository.checkOut(locationEvidence, new com.example.attendance.data.repository.AttendanceRepository.AttendanceCallback<com.example.attendance.data.model.response.CheckOutResponse>() {
                @Override
                public void onSuccess(com.example.attendance.data.model.response.CheckOutResponse res) {
                    showTicketBottomSheet(action, wifiEvidence);
                }
                @Override
                public void onError(String message) {
                    showTicketBottomSheet(action, wifiEvidence);
                }
                @Override
                public void onSavedOffline(String eventId) {
                    showTicketBottomSheet(action, wifiEvidence);
                }
            });
        }
    }

    private void showTicketBottomSheet(String action, com.example.attendance.data.model.request.WifiEvidence wifi) {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(com.example.attendance.R.layout.dialog_attendance_ticket, null);
        dialog.setContentView(dialogView);

        android.widget.TextView tvTitle = dialogView.findViewById(com.example.attendance.R.id.tvTicketTitle);
        android.widget.TextView tvTime = dialogView.findViewById(com.example.attendance.R.id.tvTicketTime);
        android.widget.TextView tvWifi = dialogView.findViewById(com.example.attendance.R.id.tvTicketWifi);
        android.widget.Button btnDone = dialogView.findViewById(com.example.attendance.R.id.btnDoneTicket);

        String titleStr;
        if ("TEMP_OUT".equals(action)) {
            titleStr = "BÁO RA NGOÀI TẠM THỜI THÀNH CÔNG";
        } else if ("TEMP_IN".equals(action)) {
            titleStr = "QUAY LẠI CA LÀM VIỆC THÀNH CÔNG";
        } else if ("CHECK_IN".equals(action)) {
            titleStr = "VÀO CA (CHECK-IN) THÀNH CÔNG";
        } else {
            titleStr = "RA CA (CHECK-OUT) THÀNH CÔNG";
        }
        tvTitle.setText(titleStr);

        String currentTimeStr = new java.text.SimpleDateFormat("HH:mm:ss - dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
        tvTime.setText(currentTimeStr);
        tvWifi.setText(wifi != null && wifi.getSsid() != null ? wifi.getSsid() : "FPT_Office_5G");

        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }
}
