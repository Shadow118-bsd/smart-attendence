package com.example.attendance.data.model.response;

public class HeartbeatResponse {
    private boolean success;
    private String status; // PRESENT, AWAY, WARNING, BREAK, etc.
    private String serverTimestamp;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getServerTimestamp() { return serverTimestamp; }
    public void setServerTimestamp(String serverTimestamp) { this.serverTimestamp = serverTimestamp; }
}
