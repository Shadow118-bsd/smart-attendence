# BUSINESS MODULES & RULE SETS — SMART ATTENDANCE SYSTEM

> **Status:** Business & Contract Specifications  
> **Target System:** Context-Aware Smart Attendance System (3-Member Architecture)  
> **Version:** 2.1.0

---

## 1. Executive Summary

Tài liệu này định nghĩa cấu trúc Module nghiệp vụ, các Vai trò hệ thống (Actors), Quy trình phân tầng dữ liệu 4 bước và danh sách **Rule Sets** chi tiết cùng **Module Contracts (JSON Schemas)** giữa 3 thành viên:
* **Member 1 (Module 1):** Android Employee App & Evidence Collector (UI/UX, GPS, Wi-Fi, MLKit Face, Room DB, Monitoring Service).
* **Member 2 (Module 2):** Backend REST API Gateway, RBAC Auth, Database CSDL 13 bảng.
* **Member 3 (Module 3):** Context-Aware Attendance Engine (State Machine, Workplace Rules Evaluator, Work Hours Calculation).

---

## 2. System Actors & Roles

1. **Employee (Nhân viên):** Thực hiện điểm danh 1-chạm (Check-in/Check-out), xin nghỉ/OT, ra ngoài tạm thời (`TEMP_OUT`), nhận cảnh báo vị trí.
2. **Manager / Supervisor (Quản lý):** Phê duyệt đơn từ, đối soát nhật ký điểm danh và các vi phạm vượt ranh giới.
3. **Foreground Service (Dịch vụ giám sát ngầm):** Tự động thu thập Wi-Fi + GPS mỗi 3 phút và gửi Heartbeat lên Server.
4. **Context Engine (Member 3 Evaluator):** Đánh giá trạng thái làm việc (`PRESENT`, `TEMPARARILY_AWAY`, `OUT_OF_BOUNDS`, `WARNING`).

---

## 3. 4-Step Layered Data Flow Pipeline

Quy trình phối hợp khép kín giữa 3 thành viên:

```
┌───────────────────────────────────────────────────────────┐
│                   STEP 1: MEMBER 1                        │
│  Thu thập Bằng chứng (Evidence): Face, Wi-Fi, GPS, Device │
└─────────────────────────────┬─────────────────────────────┘
                              │ POST Payload
                              ▼
┌───────────────────────────────────────────────────────────┐
│                   STEP 2: MEMBER 2                        │
│  Nhận REST API Payload → Chuyển tiếp tới Member 3 Engine  │
└─────────────────────────────┬─────────────────────────────┘
                              │ Evaluate Rules
                              ▼
┌───────────────────────────────────────────────────────────┐
│                   STEP 3: MEMBER 3                        │
│  Phân tích State Machine & Policy → Ra Decision / Warning │
└─────────────────────────────┬─────────────────────────────┘
                              │ Decision Result
                              ▼
┌───────────────────────────────────────────────────────────┐
│                   STEP 4: MEMBER 2                        │
│  Ghi nhận kết quả vào CSDL (Tables) → Trả HTTP Response  │
└─────────────────────────────┬─────────────────────────────┘
                              │ HTTP Response JSON
                              ▼
┌───────────────────────────────────────────────────────────┐
│               DISPLAY RESULT: MEMBER 1                    │
│  Cập nhật UI/UX / Bật Notification Cảnh báo màu đỏ ngầm  │
└───────────────────────────────────────────────────────────┘
```

---

## 4. Detailed Rule Sets Specification

### 4.1. Module 1 & 3: Workplace Presence Verification Rules
#### Rule ID: `RULE_ATTEND_001` — Check-in 3-Factor Verification Rule
* **Condition:**
  1. `Wi-Fi`: `isWifiConnected == true` AND `bssid IN (Approved_Office_WiFis)`
  2. `GPS`: `distance_to_office <= 1000m` AND `isMock == false`
  3. `Face`: `livenessPassed == true` AND `faceMatchScore >= 0.85`
* **On Pass:**
  * Xác nhận điểm danh hợp lệ.
  * Cấp `sessionId` làm việc duy nhất.
  * Hiển thị Vé Điểm Danh Điện Tử dạng BottomSheet (`Electronic Attendance Ticket`).
  * Tự động khởi chạy `AttendanceMonitoringService` ngầm.
* **On Fail:**
  * Chặn điểm danh tại nguồn.
  * Xóa tệp ảnh chụp tạm (`photoFile.delete()`).
  * Không chèn bản ghi rác vào Room DB.
  * Hiển thị BottomSheet Cảnh Báo Chuẩn Đoán (`dialog_attendance_error.xml`) kèm nguyên nhân và nút mở nhanh Cài đặt Wi-Fi/GPS.

---

### 4.2. Module 1 & 3: Continuous Presence Monitoring Rules
#### Rule ID: `RULE_HEARTBEAT_001` — Heartbeat Monitoring & Warning Rule
* **Execution Interval:** 3 phút/lần (thực thi ngầm bởi `AttendanceMonitoringService`).
* **Condition:**
  * Thu thập Wi-Fi BSSID + Tọa độ GPS hiện tại.
  * Đẩy lên endpoint `POST /api/attendance/heartbeat`.
* **Decision Matrix:**
  * `status == "PRESENT"`: Nhân viên trong vùng an toàn ➔ Giữ nguyên trạng thái bình thường.
  * `status == "TEMPORARILY_AWAY"`: Nhân viên ra ngoài khu vực Wi-Fi/GPS ➔ Bật **Notification Cảnh báo màu đỏ ngầm** kèm âm thanh: *"Cảnh báo: Bạn đang ở ngoài khu vực làm việc công ty!"*.
  * `status == "OUT_OF_BOUNDS"`: Vắng mặt quá 15 phút ➔ Ghi nhận vi phạm vào CSDL.

---

### 4.3. Module 1 & 3: Temporary Exit & Mid-Shift Return Rules
#### Rule ID: `RULE_TEMP_OUT_001` — Temporary Exit & Return Workflow
* **Biến cố 1: Ra ngoài tạm thời (`TEMP_OUT`)**:
  * Áp dụng khi nhân viên cần ra ngoài giữa ca (sửa xe, gặp khách, việc khẩn).
  * Giữ nguyên phiên làm việc `sessionId`.
  * Nhập lý do vào BottomSheet `dialog_temporary_out.xml` + Chụp FaceID chính chủ.
  * Mạng Wi-Fi không bắt buộc 100% để tránh nghẽn khi ra khỏi cổng.
* **Biến cố 2: Quay lại làm việc (`TEMP_IN`)**:
  * Áp dụng khi nhân viên trở lại văn phòng.
  * **SIẾT CHẶT 100% CẢ 3 YẾU TỐ** (`Wi-Fi công ty` + `GPS đúng bán kính` + `FaceID chính chủ`) để chống gian lận giờ công.

---

## 5. UI/UX Workflow & Electronic Attendance Ticket

1. **Dashboard Ca Làm Việc 1-Chạm (`HomeFragment`)**:
   * Thẻ `cardTodayAttendance`: Giờ vào, Giờ ra, Thời gian làm việc thực tế, Huy hiệu trạng thái (`🟢 Trong vùng Wi-Fi` / `🟧 Ra ngoài tạm thời`).
   * Nút Động 1-Chạm (`btnSmartAttendance`):
     * Chưa Check-in: `[ 🟢 VÀO CA LÀM VIỆC (CHECK-IN) ]`
     * Đã Check-in: `[ 🟧 RA CA LÀM VIỆC (CHECK-OUT) ]`
     * Đã Check-out: `[ 🔘 ĐÃ HOÀN THÀNH CA LÀM VIỆC ]`
2. **Vé Điểm Danh Điện Tử (Electronic Attendance Ticket)**:
   * Hiển thị BottomSheet ngay sau khi xác thực thành công.
   * Chứa: Ảnh chụp xác thực, Loại giao dịch (`CHECK-IN`/`CHECK-OUT`), Thời gian `dd/MM/yyyy HH:mm:ss`, Mạng Wi-Fi và Đánh giá ca (`ĐÚNG GIỜ`).

---

## 6. Module Contracts (Data Payloads & Schemas)

### 6.1. Class `LocationEvidence`
```json
{
  "latitude": 10.762622,
  "longitude": 106.660172,
  "accuracy": 5.0,
  "isMock": false,
  "provider": "gps",
  "speed": 0.0,
  "altitude": 12.5
}
```

### 6.2. Class `WifiEvidence`
```json
{
  "ssid": "FPT_Company_5G",
  "bssid": "00:1A:2B:3C:4D:5E",
  "ipAddress": "192.168.1.105",
  "isWifiConnected": true
}
```

### 6.3. Class `CheckInRequest` (Client M1 ➔ Backend M2)
```json
{
  "eventId": "e9b1a2c3-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
  "timestamp": "2026-09-23T08:00:00+07:00",
  "deviceId": "9774d56d682e549c",
  "appVersion": "1.0.0",
  "face": {
    "livenessPassed": true,
    "faceImagePath": "/data/user/0/.../face_123.jpg",
    "faceVector": [0.12, -0.45, 0.88]
  },
  "wifi": {
    "ssid": "FPT_Company_5G",
    "bssid": "00:1A:2B:3C:4D:5E",
    "ipAddress": "192.168.1.105",
    "isWifiConnected": true
  },
  "location": {
    "latitude": 10.762622,
    "longitude": 106.660172,
    "accuracy": 5.0,
    "isMock": false,
    "provider": "gps"
  }
}
```

### 6.4. Class `HeartbeatRequest` (Foreground Service M1 ➔ Backend M2)
```json
{
  "eventId": "f8a2b3c4-5d6e-7f8a-9b0c-1d2e3f4a5b6c",
  "sessionId": "SESSION_20260923_001",
  "timestamp": "2026-09-23T09:15:00+07:00",
  "deviceId": "9774d56d682e549c",
  "appVersion": "1.0.0",
  "wifi": {
    "ssid": "FPT_Company_5G",
    "bssid": "00:1A:2B:3C:4D:5E",
    "ipAddress": "192.168.1.105",
    "isWifiConnected": true
  },
  "location": {
    "latitude": 10.762622,
    "longitude": 106.660172,
    "accuracy": 5.0,
    "isMock": false,
    "provider": "gps"
  }
}
```

### 6.5. Standard API Response (`ApiResponse<T>`)
```json
{
  "success": true,
  "message": "Điểm danh vào ca thành công.",
  "data": {
    "sessionId": "SESSION_20260923_001",
    "checkInTime": "2026-09-23T08:00:00+07:00",
    "status": "PRESENT"
  }
}
```
