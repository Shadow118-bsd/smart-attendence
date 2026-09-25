package com.example.attendance.data.model.response;

public class CheckOutResponse {
    private boolean success;
    private String message;
    private String checkOutTime;
    private String totalWorkingHours;

    public CheckOutResponse() {}

    public CheckOutResponse(boolean success, String message, String totalWorkingHours) {
        this.success = success;
        this.message = message;
        this.totalWorkingHours = totalWorkingHours;
    }

    public CheckOutResponse(boolean success, String message, String checkOutTime, String totalWorkingHours) {
        this.success = success;
        this.message = message;
        this.checkOutTime = checkOutTime;
        this.totalWorkingHours = totalWorkingHours;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(String checkOutTime) { this.checkOutTime = checkOutTime; }

    public String getTotalWorkingHours() { return totalWorkingHours; }
    public void setTotalWorkingHours(String totalWorkingHours) { this.totalWorkingHours = totalWorkingHours; }
}
