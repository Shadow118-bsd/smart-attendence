package com.example.attendance.data.model.response;

public class CheckInResponse {
    private boolean success;
    private String sessionId;
    private String status; // PRESENT, AWAY, etc.
    private String message;
    private String checkInTime;

    public CheckInResponse() {}

    public CheckInResponse(boolean success, String sessionId, String status, String message, String checkInTime) {
        this.success = success;
        this.sessionId = sessionId;
        this.status = status;
        this.message = message;
        this.checkInTime = checkInTime;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCheckInTime() { return checkInTime; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }
}
