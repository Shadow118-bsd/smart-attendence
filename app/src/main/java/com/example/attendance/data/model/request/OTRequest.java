package com.example.attendance.data.model.request;

public class OTRequest {
    private String employeeName;
    private String otDate;
    private String shiftName;
    private String startTime;
    private boolean startNextDay;
    private String endTime;
    private boolean endNextDay;
    private String reason;
    private String reasonDetail;

    public OTRequest() {}

    public OTRequest(String employeeName, String otDate, String shiftName, String startTime, boolean startNextDay, String endTime, boolean endNextDay, String reason, String reasonDetail) {
        this.employeeName = employeeName;
        this.otDate = otDate;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.startNextDay = startNextDay;
        this.endTime = endTime;
        this.endNextDay = endNextDay;
        this.reason = reason;
        this.reasonDetail = reasonDetail;
    }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getOtDate() { return otDate; }
    public void setOtDate(String otDate) { this.otDate = otDate; }

    public String getShiftName() { return shiftName; }
    public void setShiftName(String shiftName) { this.shiftName = shiftName; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public boolean isStartNextDay() { return startNextDay; }
    public void setStartNextDay(boolean startNextDay) { this.startNextDay = startNextDay; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public boolean isEndNextDay() { return endNextDay; }
    public void setEndNextDay(boolean endNextDay) { this.endNextDay = endNextDay; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getReasonDetail() { return reasonDetail; }
    public void setReasonDetail(String reasonDetail) { this.reasonDetail = reasonDetail; }
}
