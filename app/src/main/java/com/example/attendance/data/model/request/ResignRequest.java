package com.example.attendance.data.model.request;

public class ResignRequest {
    private String resignReason;
    private String reasonDetail;
    private String effectiveDate;
    private String lastWorkingDate;
    private String note;
    private boolean isCommitted;

    public ResignRequest() {}

    public ResignRequest(String resignReason, String reasonDetail, String effectiveDate, String lastWorkingDate, String note, boolean isCommitted) {
        this.resignReason = resignReason;
        this.reasonDetail = reasonDetail;
        this.effectiveDate = effectiveDate;
        this.lastWorkingDate = lastWorkingDate;
        this.note = note;
        this.isCommitted = isCommitted;
    }

    public String getResignReason() { return resignReason; }
    public void setResignReason(String resignReason) { this.resignReason = resignReason; }

    public String getReasonDetail() { return reasonDetail; }
    public void setReasonDetail(String reasonDetail) { this.reasonDetail = reasonDetail; }

    public String getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; }

    public String getLastWorkingDate() { return lastWorkingDate; }
    public void setLastWorkingDate(String lastWorkingDate) { this.lastWorkingDate = lastWorkingDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public boolean isCommitted() { return isCommitted; }
    public void setCommitted(boolean committed) { isCommitted = committed; }
}
