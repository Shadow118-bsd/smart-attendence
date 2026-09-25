package com.example.attendance.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

public class FaceService {
    private static final String TAG = "FaceService";

    public enum FaceVerificationStatus {
        FACE_VERIFIED,
        FACE_NOT_MATCHED,
        LIVENESS_FAILED,
        FACE_NOT_DETECTED
    }

    public static class FaceVerificationResult {
        private final FaceVerificationStatus status;
        private final boolean livenessPassed;
        private final String message;

        public FaceVerificationResult(FaceVerificationStatus status, boolean livenessPassed, String message) {
            this.status = status;
            this.livenessPassed = livenessPassed;
            this.message = message;
        }

        public FaceVerificationStatus getStatus() { return status; }
        public boolean isLivenessPassed() { return livenessPassed; }
        public String getMessage() { return message; }
    }

    public interface FaceCallback {
        void onFaceAnalyzed(FaceVerificationResult result);
    }

    private final FaceDetector detector;

    public FaceService(Context context) {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setMinFaceSize(0.1f)
                .build();
        this.detector = FaceDetection.getClient(options);
    }

    @SuppressLint("UnsafeOptInUsageError")
    public void analyzeImageProxy(ImageProxy imageProxy, FaceCallback callback) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage == null) {
            imageProxy.close();
            callback.onFaceAnalyzed(new FaceVerificationResult(FaceVerificationStatus.FACE_NOT_DETECTED, false, "Không lấy được hình ảnh từ Camera."));
            return;
        }

        InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
        processInputImage(image, () -> imageProxy.close(), callback);
    }

    public void analyzeFaceBitmap(Bitmap bitmap, FaceCallback callback) {
        if (bitmap == null) {
            callback.onFaceAnalyzed(new FaceVerificationResult(FaceVerificationStatus.FACE_NOT_DETECTED, false, "Hình ảnh trống."));
            return;
        }

        InputImage image = InputImage.fromBitmap(bitmap, 0);
        processInputImage(image, () -> {}, callback);
    }

    private void processInputImage(InputImage image, Runnable onComplete, FaceCallback callback) {
        detector.process(image)
                .addOnSuccessListener(faces -> {
                    onComplete.run();
                    if (faces == null || faces.isEmpty()) {
                        callback.onFaceAnalyzed(new FaceVerificationResult(
                                FaceVerificationStatus.FACE_NOT_DETECTED,
                                false,
                                "Không tìm thấy khuôn mặt trong khung hình. Vui lòng giữ thẳng ống kính."
                        ));
                        return;
                    }

                    // Phân tích Passive Liveness cho khuôn mặt thực tế
                    Face face = faces.get(0);
                    Float leftEye = face.getLeftEyeOpenProbability();
                    Float rightEye = face.getRightEyeOpenProbability();

                    boolean livenessPassed = true;
                    String message = "Xác thực khuôn mặt thành công!";

                    // Nếu thiết bị hỗ trợ Eye Classification, kiểm tra mắt có mở không
                    if (leftEye != null && rightEye != null) {
                        if (leftEye < 0.2f && rightEye < 0.2f) {
                            livenessPassed = false;
                            message = "Xác thực thất bại: Mắt đang nhắm hoặc khuôn mặt bị che khuất.";
                        }
                    }

                    if (livenessPassed) {
                        callback.onFaceAnalyzed(new FaceVerificationResult(
                                FaceVerificationStatus.FACE_VERIFIED,
                                true,
                                message
                        ));
                    } else {
                        callback.onFaceAnalyzed(new FaceVerificationResult(
                                FaceVerificationStatus.LIVENESS_FAILED,
                                false,
                                message
                        ));
                    }
                })
                .addOnFailureListener(e -> {
                    onComplete.run();
                    Log.e(TAG, "Face detection failed: " + e.getMessage());
                    callback.onFaceAnalyzed(new FaceVerificationResult(
                            FaceVerificationStatus.FACE_NOT_DETECTED,
                            false,
                            "Lỗi phân tích khuôn mặt: " + e.getMessage()
                    ));
                });
    }

    public static String compressAndEncodeBitmap(Bitmap bitmap) {
        if (bitmap == null) return "";
        int maxWidth = 1080;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > maxWidth) {
            float ratio = (float) maxWidth / width;
            width = maxWidth;
            height = Math.round(height * ratio);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP);
    }
}
