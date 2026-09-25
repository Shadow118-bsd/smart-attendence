package com.example.attendance.data.model.request;

public class LeaveRequest {
    private String leaveType;       // e.g., "[P] Nghỉ phép năm"
    private String fromDate;        // e.g., "17/09/2026"
    private String toDate;          // e.g., "17/09/2026"
    private boolean declareDaily;   // Khai báo từng ngày
    private double hours;           // e.g., 8
    private String attachmentNote;  // Ghi chú tệp đính kèm
    private String reason;          // Lý do chọn
    private String reasonDetail;    // Lý do chi tiết

    public LeaveRequest() {}

    public LeaveRequest(String leaveType, String fromDate, String toDate, boolean declareDaily, double hours, String attachmentNote, String reason, String reasonDetail) {
        this.leaveType = leaveType;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.declareDaily = declareDaily;
        this.hours = hours;
        this.attachmentNote = attachmentNote;
        this.reason = reason;
        this.reasonDetail = reasonDetail;
    }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public String getFromDate() { return fromDate; }
    public void setFromDate(String fromDate) { this.fromDate = fromDate; }

    public String getToDate() { return toDate; }
    public void setToDate(String toDate) { this.toDate = toDate; }

    public boolean isDeclareDaily() { return declareDaily; }
    public void setDeclareDaily(boolean declareDaily) { this.declareDaily = declareDaily; }

    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }

    public String getAttachmentNote() { return attachmentNote; }
    public void setAttachmentNote(String attachmentNote) { this.attachmentNote = attachmentNote; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getReasonDetail() { return reasonDetail; }
    public void setReasonDetail(String reasonDetail) { this.reasonDetail = reasonDetail; }
}
