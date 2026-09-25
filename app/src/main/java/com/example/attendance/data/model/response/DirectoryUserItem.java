package com.example.attendance.data.model.response;

public class DirectoryUserItem {
    private String id;
    private String fullName;      // e.g. "Phạm Văn Do"
    private String department;    // e.g. "CTS-NMSVNM - Ban Phát triển vùng nguyên liệu"
    private String avatarUrl;

    public DirectoryUserItem() {}

    public DirectoryUserItem(String id, String fullName, String department, String avatarUrl) {
        this.id = id;
        this.fullName = fullName;
        this.department = department;
        this.avatarUrl = avatarUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
