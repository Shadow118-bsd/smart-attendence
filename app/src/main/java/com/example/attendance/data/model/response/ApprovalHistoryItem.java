package com.example.attendance.data.model.response;

public class ApprovalHistoryItem {
    private String id;
    private String employeeName;   // e.g. "Nguyễn Thị Ngọc Hạnh"
    private String requestType;    // e.g. "Đăng ký làm thêm"
    private String status;         // e.g. "Phê duyệt"
    private String requestDate;    // e.g. "01/09/2026"
    private String otShift;        // e.g. "06:00 - 14:00"
    private String reason;         // e.g. "Trực lễ"
    private String detailReason;   // e.g. "Làm thêm giờ ngày lễ"
    private String approvalDate;   // e.g. "15/09/2026"
    private String approverName;   // e.g. "Nguyễn Thị Ngọc Hạnh"
    private String approverRole;   // e.g. "Nhân viên KCS trạm sữa"

    public ApprovalHistoryItem() {}

    public ApprovalHistoryItem(String id, String employeeName, String requestType, String status, String requestDate, String otShift, String reason, String detailReason, String approvalDate, String approverName, String approverRole) {
        this.id = id;
        this.employeeName = employeeName;
        this.requestType = requestType;
        this.status = status;
        this.requestDate = requestDate;
        this.otShift = otShift;
        this.reason = reason;
        this.detailReason = detailReason;
        this.approvalDate = approvalDate;
        this.approverName = approverName;
        this.approverRole = approverRole;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }

    public String getOtShift() { return otShift; }
    public void setOtShift(String otShift) { this.otShift = otShift; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDetailReason() { return detailReason; }
    public void setDetailReason(String detailReason) { this.detailReason = detailReason; }

    public String getApprovalDate() { return approvalDate; }
    public void setApprovalDate(String approvalDate) { this.approvalDate = approvalDate; }

    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }

    public String getApproverRole() { return approverRole; }
    public void setApproverRole(String approverRole) { this.approverRole = approverRole; }
}
