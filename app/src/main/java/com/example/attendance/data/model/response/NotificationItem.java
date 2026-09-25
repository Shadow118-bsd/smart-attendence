package com.example.attendance.data.model.response;

public class NotificationItem {
    private String id;
    private String senderName;    // "Ngô Phương Thịnh" / "Nguyễn Thị Nhị Hòa"
    private String content;       // "Đăng ký làm thêm đã được duyệt" / "Đăng ký nghỉ đã được duyệt"
    private String timeString;    // "09:37 - 17/09/2026"
    private boolean isRead;

    public NotificationItem() {}

    public NotificationItem(String id, String senderName, String content, String timeString, boolean isRead) {
        this.id = id;
        this.senderName = senderName;
        this.content = content;
        this.timeString = timeString;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTimeString() { return timeString; }
    public void setTimeString(String timeString) { this.timeString = timeString; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
