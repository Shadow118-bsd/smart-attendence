package com.example.attendance.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "SmartAttendancePrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_SESSION_ID = "current_session_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_EMPLOYEE_ID = "employee_id";

    private final SharedPreferences prefs;

    private static final String KEY_DEVICE_ID = "device_id";

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getDeviceId() {
        String deviceId = prefs.getString(KEY_DEVICE_ID, null);
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = java.util.UUID.randomUUID().toString();
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply();
        }
        return deviceId;
    }

    public void saveAuthToken(String accessToken, String refreshToken) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void saveCurrentSessionId(String sessionId) {
        prefs.edit().putString(KEY_CURRENT_SESSION_ID, sessionId).apply();
    }

    public String getCurrentSessionId() {
        return prefs.getString(KEY_CURRENT_SESSION_ID, null);
    }

    private static final String KEY_USER_ROLE = "user_role";

    public void saveUserInfo(String employeeId, String name) {
        saveUserInfo(employeeId, name, "EMPLOYEE");
    }

    public void saveUserInfo(String employeeId, String name, String role) {
        prefs.edit()
                .putString(KEY_EMPLOYEE_ID, employeeId)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_ROLE, role)
                .apply();
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Nhân viên");
    }

    public String getEmployeeId() {
        return prefs.getString(KEY_EMPLOYEE_ID, "");
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, "EMPLOYEE");
    }

    public boolean isManager() {
        String role = getUserRole();
        return "MANAGER".equalsIgnoreCase(role) || "HR".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    private static final String KEY_OFFICE_LAT = "office_latitude";
    private static final String KEY_OFFICE_LNG = "office_longitude";
    private static final String KEY_GEOFENCE_RADIUS = "geofence_radius";
    private static final String KEY_APPROVED_BSSIDS = "approved_bssids";

    public void saveWorkplaceConfig(double lat, double lng, float radiusMeters, String approvedBssids) {
        prefs.edit()
                .putFloat(KEY_OFFICE_LAT, (float) lat)
                .putFloat(KEY_OFFICE_LNG, (float) lng)
                .putFloat(KEY_GEOFENCE_RADIUS, radiusMeters)
                .putString(KEY_APPROVED_BSSIDS, approvedBssids)
                .apply();
    }

    public double getOfficeLatitude() {
        return prefs.getFloat(KEY_OFFICE_LAT, 10.7769f);
    }

    public double getOfficeLongitude() {
        return prefs.getFloat(KEY_OFFICE_LNG, 106.7009f);
    }

    public float getGeofenceRadius() {
        return prefs.getFloat(KEY_GEOFENCE_RADIUS, 1000.0f);
    }

    public String getApprovedBssids() {
        return prefs.getString(KEY_APPROVED_BSSIDS, "");
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
