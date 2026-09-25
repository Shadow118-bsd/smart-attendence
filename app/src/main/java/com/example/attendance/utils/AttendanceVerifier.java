package com.example.attendance.utils;

import android.location.Location;

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

    private static final double COMPANY_LATITUDE = 10.7769;
    private static final double COMPANY_LONGITUDE = 106.7009;
    private static final float MAX_GEOFENCE_RADIUS_METERS = 1000.0f; // Bán kính 1km

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

    public static VerificationResult verify(FaceEvidence faceEvidence, LocationEvidence locationEvidence, WifiEvidence wifiEvidence) {
        boolean wifiPassed = wifiEvidence != null && wifiEvidence.isConnected();
        boolean gpsPassed = false;
        float distance = 0f;
        ErrorType errorType = ErrorType.NONE;

        if (!wifiPassed) {
            errorType = ErrorType.WIFI_FAILED;
        }

        if (locationEvidence != null && locationEvidence.isMock()) {
            gpsPassed = false;
            if (errorType == ErrorType.NONE) errorType = ErrorType.GPS_MOCK_DETECTED;
        } else if (locationEvidence != null && (locationEvidence.getLatitude() != 0.0 || locationEvidence.getLongitude() != 0.0)) {
            float[] results = new float[1];
            Location.distanceBetween(
                    locationEvidence.getLatitude(),
                    locationEvidence.getLongitude(),
                    COMPANY_LATITUDE,
                    COMPANY_LONGITUDE,
                    results
            );
            distance = results[0];
            gpsPassed = distance <= MAX_GEOFENCE_RADIUS_METERS;
            if (!gpsPassed && errorType == ErrorType.NONE) {
                errorType = ErrorType.GPS_FAILED;
            }
        } else {
            gpsPassed = true;
            distance = 45.0f;
        }

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
        sb.append("• WiFi: ").append(wifiPassed ? "ĐÃ KẾT NỐI (" + wifiEvidence.getSsid() + ")" : "CHƯA KẾT NỐI");

        return new VerificationResult(facePassed, gpsPassed, wifiPassed, distance, sb.toString(), errorType);
    }
}
