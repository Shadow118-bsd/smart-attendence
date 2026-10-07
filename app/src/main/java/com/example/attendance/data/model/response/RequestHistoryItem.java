package com.example.attendance.data.model.response;

public class RequestHistoryItem {
    private String id;
    private String title;          // e.g. "Đăng ký làm thêm"
    private String status;         // e.g. "Phê duyệt" / "Đã duyệt"
    private String date;           // e.g. "01/09/2026"
    private String reason;         // e.g. "Lý do Làm thêm giờ ngày lễ"
    private String detailReason;   // e.g. "Làm thêm giờ ngày lễ"
    private String otShift;        // e.g. "06:00 - 14:00"
    private String approverName;   // e.g. "Ngô Phương Thịnh" / "Nguyễn Thị Ngọc Hạnh"
    private String approverRole;   // e.g. "Giám đốc Nhà máy"
    private String approvalDate;   // e.g. "15/09/2026"

    public RequestHistoryItem() {}

    public RequestHistoryItem(String id, String title, String date, String reason, String status, String approvalDate) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.reason = reason;
        this.status = status;
        this.approvalDate = approvalDate;
    }

    public RequestHistoryItem(String id, String title, String status, String date, String reason, String detailReason, String otShift, String approverName, String approverRole, String approvalDate) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.date = date;
        this.reason = reason;
        this.detailReason = detailReason;
        this.otShift = otShift;
        this.approverName = approverName;
        this.approverRole = approverRole;
        this.approvalDate = approvalDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDetailReason() { return detailReason; }
    public void setDetailReason(String detailReason) { this.detailReason = detailReason; }

    public String getOtShift() { return otShift; }
    public void setOtShift(String otShift) { this.otShift = otShift; }

    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }

    public String getApproverRole() { return approverRole; }
    public void setApproverRole(String approverRole) { this.approverRole = approverRole; }

    public String getApprovalDate() { return approvalDate; }
    public void setApprovalDate(String approvalDate) { this.approvalDate = approvalDate; }
}
