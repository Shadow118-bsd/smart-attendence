package com.example.attendance.utils;

import com.example.attendance.data.model.request.FaceEvidence;
import com.example.attendance.data.model.request.LocationEvidence;
import com.example.attendance.data.model.request.WifiEvidence;

public class AttendanceVerifier {
    public enum ErrorType {
        NONE,
        WIFI_FAILED,
        GPS_FAILED,
        GPS_MOCK_DETECTED,
        FACE_FAILED,
        LIVENESS_FAILED
    }

    private static final double DEFAULT_COMPANY_LATITUDE = 10.7769;
    private static final double DEFAULT_COMPANY_LONGITUDE = 106.7009;
    private static final float DEFAULT_MAX_GEOFENCE_RADIUS_METERS = 1000.0f; // Bán kính 1km

    public static class VerificationResult {
        private final boolean facePassed;
        private final boolean gpsPassed;
        private final boolean wifiPassed;
        private final float distanceMeters;
        private final String detailsMessage;
        private final ErrorType errorType;

        public VerificationResult(boolean facePassed, boolean gpsPassed, boolean wifiPassed, float distanceMeters, String detailsMessage, ErrorType errorType) {
            this.facePassed = facePassed;
            this.gpsPassed = gpsPassed;
            this.wifiPassed = wifiPassed;
            this.distanceMeters = distanceMeters;
            this.detailsMessage = detailsMessage;
            this.errorType = errorType;
        }

        public boolean isFacePassed() { return facePassed; }
        public boolean isGpsPassed() { return gpsPassed; }
        public boolean isWifiPassed() { return wifiPassed; }
        public float getDistanceMeters() { return distanceMeters; }
        public String getDetailsMessage() { return detailsMessage; }
        public ErrorType getErrorType() { return errorType; }

        public boolean isFullyValid() {
            return facePassed && gpsPassed && wifiPassed;
        }
    }

    /**
     * Thuật toán tính khoảng cách địa lý theo công thức Haversine thuần Java.
     * Tự chủ không phụ thuộc SDK Android Location.distanceBetween.
     */
    public static double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Bán kính Trái Đất (mét)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public static VerificationResult verify(FaceEvidence faceEvidence, LocationEvidence locationEvidence, WifiEvidence wifiEvidence) {
        return verify(faceEvidence, locationEvidence, wifiEvidence, DEFAULT_COMPANY_LATITUDE, DEFAULT_COMPANY_LONGITUDE, DEFAULT_MAX_GEOFENCE_RADIUS_METERS, null);
    }

    public static VerificationResult verify(FaceEvidence faceEvidence, LocationEvidence locationEvidence, WifiEvidence wifiEvidence, double officeLat, double officeLng, float maxRadiusMeters, String approvedBssids) {
        ErrorType errorType = ErrorType.NONE;

        // 1. Kiểm tra Bằng chứng Mạng Wi-Fi (Wi-Fi Evidence & BSSID Verification)
        boolean wifiPassed = wifiEvidence != null && wifiEvidence.isConnected();
        if (wifiPassed && approvedBssids != null && !approvedBssids.trim().isEmpty()) {
            String currentBssid = wifiEvidence.getBssid();
            if (currentBssid == null || currentBssid.isEmpty() || "UNKNOWN".equalsIgnoreCase(currentBssid) || !approvedBssids.contains(currentBssid)) {
                wifiPassed = false;
            }
        }
        if (!wifiPassed) {
            errorType = ErrorType.WIFI_FAILED;
        }

        // 2. Kiểm tra Bằng chứng Vị trí GPS (GPS & Geofence Verification)
        boolean gpsPassed = false;
        float distance = 0f;

        if (locationEvidence == null || (locationEvidence.getLatitude() == 0.0 && locationEvidence.getLongitude() == 0.0)) {
            // Sửa lỗ hổng an ninh: Không bypass GPS khi thiếu tọa độ
            gpsPassed = false;
            if (errorType == ErrorType.NONE) {
                errorType = ErrorType.GPS_FAILED;
            }
        } else if (locationEvidence.isMock()) {
            gpsPassed = false;
            if (errorType == ErrorType.NONE) {
                errorType = ErrorType.GPS_MOCK_DETECTED;
            }
        } else {
            distance = (float) calculateHaversineDistance(
                    locationEvidence.getLatitude(),
                    locationEvidence.getLongitude(),
                    officeLat,
                    officeLng
            );
            gpsPassed = distance <= maxRadiusMeters;
            if (!gpsPassed && errorType == ErrorType.NONE) {
                errorType = ErrorType.GPS_FAILED;
            }
        }

        // 3. Kiểm tra Bằng chứng Sinh trắc Khuôn mặt (FaceID Evidence Verification)
        boolean facePassed = faceEvidence != null && faceEvidence.isVerified();
        if (!facePassed && errorType == ErrorType.NONE) {
            if (faceEvidence != null && !faceEvidence.isLivenessPassed()) {
                errorType = ErrorType.LIVENESS_FAILED;
            } else {
                errorType = ErrorType.FACE_FAILED;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("• FaceID: ").append(facePassed ? "ĐẠT" : "KHÔNG ĐẠT").append("\n");
        sb.append("• GPS: ").append(gpsPassed ? "HỢP LỆ (" + Math.round(distance) + "m)" : "KHÔNG ĐẠT").append("\n");
        sb.append("• WiFi: ").append(wifiPassed ? "ĐÃ KẾT NỐI (" + (wifiEvidence != null ? wifiEvidence.getSsid() : "N/A") + ")" : "CHƯA KẾT NỐI/SAI BSSID");

        return new VerificationResult(facePassed, gpsPassed, wifiPassed, distance, sb.toString(), errorType);
    }
}
