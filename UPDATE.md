# SMART ATTENDANCE SYSTEM — MODULE 1 PROGRESS & CHANGE TRACKING (UPDATE.MD)

> **Status:** Active Tracking & Delta Analysis Document  
> **Scope:** Progress Tracking, Feature Analysis & Modification Log for Module 1  
> **Reference Baseline:** Compare against 3 Core Docs in `docs/` (`01-SYSTEM_RULES`, `02-TECHNOLOGY_PROFILE`, `03-BUSINESS_MODULES`)  
> **Last Updated:** 2026-10-02

---

## 1. Purpose of This File (Mục Đích Sử Dụng)

File `UPDATE.md` này đóng vai trò là **Nhật ký Phân tích, Tiến độ & Ghi nhận Thay đổi** động của hệ thống. 
* Khi có bất kỳ thay đổi nào về luồng nghiệp vụ, giao diện, hoặc logic kỹ thuật trong Module 1, thành viên phát triển sẽ cập nhật vào file này.
* Dùng file này để **đối soát (compare & contrast)** xem các thay đổi mới có vi phạm hoặc cần cập nhật lại bộ 3 file quy định gốc trong `docs/` hay không.

```
┌───────────────────────────────────────────────────────────┐
│                 3 BASELINE FILES IN docs/                 │
│ 01-SYSTEM_RULES  |  02-TECH_PROFILE  |  03-BUSINESS_MODULES│
└─────────────────────────────▲─────────────────────────────┘
                              │ Compare & Validate
                              │
┌─────────────────────────────┴─────────────────────────────┐
│                    d:\smart-attendence\                   │
│                         UPDATE.md                         │
│   (Nhật ký phân tích tiến độ, tính năng & thay đổi module) │
└─────────────────────────────▲─────────────────────────────┘
                              │ Update on Code Changes
                              │
┌─────────────────────────────┴─────────────────────────────┐
│                   ANDROID JAVA SOURCE CODE                │
│             app/src/main/java/com/example/attendance/    │
└───────────────────────────────────────────────────────────┘
```

---

## 2. Current Implementation Status vs 3 Baseline Docs (Bảng Đối Soát Hiện Trạng)

| Hạng mục / Tính năng | Hiện trạng triển khai trong Code | Đã đối soát với `docs/` | Nhận xét & Đánh giá |
| :--- | :--- | :--- | :--- |
| **Kiến trúc 4 tầng Clean Architecture** | Đã phân tách `ui`, `viewmodel`, `data`, `service`, `worker`, `utils` | ✅ Dat `01-SYSTEM_RULES` | Tầng Presentation chỉ lắng nghe LiveData, không gọi API/GPS trực tiếp. |
| **Bằng chứng Vị trí (GPS)** | `LocationService.java` thu thập Lat/Lng, Accuracy, Speed, Altitude | ✅ Dat `03-BUSINESS_MODULES` | Chuẩn hóa DTO `LocationEvidence`. Có kiểm tra Mock Location (`isMock`). |
| **Bằng chứng Wi-Fi** | `WifiService.java` thu thập BSSID, SSID, IP Address | ✅ Dat `03-BUSINESS_MODULES` | Chuẩn hóa DTO `WifiEvidence`. Kiểm tra trạng thái kết nối Wi-Fi thực tế. |
| **Sinh trắc khuôn mặt (FaceID)** | `FaceService.java` tích hợp Google MLKit Face Detection | ✅ Dat `03-BUSINESS_MODULES` | Thu thập vector khuôn mặt & kiểm tra liveness thực thể sống. |
| **Zero-Trash Data Policy** | Xóa file ảnh tạm `photoFile.delete()`, hiển thị `dialog_attendance_error.xml` | ✅ Dat `01-SYSTEM_RULES` | Không chèn bản ghi rác vào Room DB hoặc gửi API khi 3 yếu tố không đạt. |
| **Giám sát ngầm Heartbeat** | `AttendanceMonitoringService.java` chạy ngầm | ✅ Dat `02-TECHNOLOGY_PROFILE` | Tự động gửi Heartbeat 3 phút/lần khi đã Check-in, dừng khi Check-out. |
| **Bật Cảnh báo vị trí màu đỏ** | Notification Manager khi nhận `TEMPORARILY_AWAY` từ Server | ✅ Dat `03-BUSINESS_MODULES` | Phát thông báo âm thanh màu đỏ thông báo nhân viên vượt ranh giới. |
| **Lưu trữ Offline & Sync** | Room DB (`AttendanceRecordEntity`) + `SyncWorker` | ✅ Dat `01-SYSTEM_RULES` | Tự động đồng bộ ngầm khi `NetworkChangeReceiver` phát hiện có mạng lại. |
| **Luồng Ra ngoài tạm thời** | BottomSheet `dialog_temporary_out.xml` (`TEMP_OUT`/`TEMP_IN`) | ✅ Dat `03-BUSINESS_MODULES` | Phân biệt linh hoạt Wi-Fi khi `TEMP_OUT` và siết chặt 100% 3 yếu tố khi `TEMP_IN`. |
| **Vé Điểm Danh Điện Tử** | BottomSheet vé điện tử hiển thị tại Trang chủ | ✅ Dat `03-BUSINESS_MODULES` | Hiển thị ảnh chụp, thời gian `dd/MM/yyyy HH:mm:ss`, mạng Wi-Fi và đánh giá ca. |

---

## 3. Detailed Progress Breakdown (Chi Tiết Tiến Độ Đã Hoàn Thành)

### 3.1. Giao diện Người dùng (Presentation Layer)
* [x] **HomeFragment (`fragment_home.xml`):** Thẻ thông tin hôm nay `cardTodayAttendance`, Huy hiệu trạng thái động, Nút 1-Chạm `btnSmartAttendance`, Lưới 6 tính năng cân bằng.
* [x] **Electronic Ticket BottomSheet (`dialog_attendance_ticket.xml`):** Vé điện tử xác nhận giờ công thời gian thực.
* [x] **Diagnostic Warning BottomSheet (`dialog_attendance_error.xml`):** Khung hiển thị chi tiết lý do lỗi + Nút mở nhanh Cài đặt Wi-Fi/GPS.
* [x] **Temporary Exit BottomSheet (`dialog_temporary_out.xml`):** Nhập lý do ra ngoài tạm thời giữa ca.
* [x] **Activity Quản lý Phụ:** Profile (`ProfileActivity`), Lịch sử (`AttendanceHistoryActivity`), Nhật ký In/Out (`InOutDataActivity`), Yêu cầu phép/OT/thôi việc (`LeaveRequestActivity`, `OvertimeRequestActivity`, `ResignRequestActivity`).

### 3.2. Tầng Dữ liệu & Dịch vụ Ngầm (Infra & Services Layer)
* [x] **AttendanceMonitoringService:** Foreground Service duy trì Notification icon, Timer 3 phút gửi `HeartbeatRequest`.
* [x] **SyncWorker & NetworkChangeReceiver:** WorkManager quét `PENDING` events và tự động đẩy API khi reconnect.
* [x] **SessionManager:** Lưu trữ SharedPreferences cho JWT Token, Employee Profile, active `sessionId`.

---

## 4. Gap Analysis & Open Items (Khoảng Trống Cần Hoàn Thiện Tiếp Theo)

Dưới đây là các mục cần đối soát và kiểm thử tích hợp liên module với **Member 2 (Backend)** và **Member 3 (Context Engine)**:

| STT | Hạng mục cần làm tiếp | Module liên quan | Mục tiêu đạt được |
| :--- | :--- | :--- | :--- |
| 1 | **Integration Test API thật** | Member 1 ↔ Member 2 | Test API `/api/attendance/check-in` và `/api/attendance/heartbeat` với Server Backend thật. |
| 2 | **Xử lý Timeout & Server Offline** | Member 1 | Thêm fallback tự động chuyển sang lưu Room DB `PendingEventEntity` khi Server trả về HTTP `503` hoặc Timeout. |
| 3 | **Cấu hình Geofence động từ Server** | Member 1 ↔ Member 3 | Thay vì hardcode bán kính 1000m, nhận bán kính geofence linh hoạt từ API đăng nhập/profile của Member 2. |
| 4 | **Tối ưu Pin cho Service Heartbeat** | Member 1 | Đảm bảo `AttendanceMonitoringService` không bị Android OS Doze Mode hủy trên các dòng máy Xiaomi/Samsung. |

---

## 5. Change Tracking Log (Nhật Ký Ghi Nhận Thay Đổi & Nâng Cấp Code)

### Change Record #1 — 02/10/2026 (Phase 1 Refactoring Execution)
* **Người thực hiện:** AI Assistant & Member 1
* **Mô tả thay đổi:** 
  1. **Khắc phục lỗ hổng bypass GPS:** Triệt tiêu hoàn toàn logic gán cứng `gpsPassed = true; distance = 45.0m;` khi thiếu vị trí. Nếu `locationEvidence == null` hoặc tọa độ là `0.0, 0.0`, trả về `gpsPassed = false` và `errorType = ErrorType.GPS_FAILED`.
  2. **Thuật toán Haversine Distance:** Đóng gói hàm `calculateHaversineDistance()` thuần Java tính khoảng cách địa lý chính xác mà không phụ thuộc SDK Android.
  3. **Thuật toán xác thực Wi-Fi BSSID:** Bổ sung cơ chế đối soát `wifiEvidence.getBssid()` với danh sách BSSID công ty được phê duyệt (`approvedBssids`).
  4. **Dynamic Workplace Config:** Bổ sung phương thức lưu trữ & truy xuất cấu hình tọa độ văn phòng, bán kính Geofence và danh sách BSSID trong `SessionManager`.
* **Tệp ảnh hưởng:**
  * [`app/src/main/java/com/example/attendance/utils/AttendanceVerifier.java`](file:///d:/smart-attendence/app/src/main/java/com/example/attendance/utils/AttendanceVerifier.java)
  * [`app/src/main/java/com/example/attendance/utils/SessionManager.java`](file:///d:/smart-attendence/app/src/main/java/com/example/attendance/utils/SessionManager.java)
* **Đối soát với `docs/`:**
  * [x] Tuân thủ nghiêm ngặt `01-SYSTEM_RULES` (Nguyên tắc Zero-Trash Data & Chặn gian lận tại nguồn).
  * [x] Khớp với `02-TECHNOLOGY_PROFILE` (Phân tầng Utility & Session Management Java).
  * [x] Đạt chuẩn `03-BUSINESS_MODULES` (Quy tắc `RULE_ATTEND_001` - Workplace Presence Verification).

### Change Record #2 — 02/10/2026 (Phase 2 Refactoring Execution)
* **Người thực hiện:** AI Assistant & Member 1
* **Mô tả thay đổi:** 
  1. **Thuật toán Multi-tier Mock Location Detection:** Tích hợp kiểm tra Fake GPS 4 tầng trong `LocationService.java` (Level 1: SDK cờ mock, Level 2: Location Extras, Level 3: System Settings legacy, Level 4: **Teleportation Speed Anomaly Check** phát hiện dịch chuyển bất thường > 150 km/h).
  2. **Thuật toán Head Euler Pose Angle & Multi-factor Liveness:** Nâng cấp `FaceService.java` với tính năng kiểm tra góc xoay/nghiêng đầu (Head Euler Y/Z Angle <= 30 độ) để bắt buộc chụp diện chẩn trực diện và trả về xác thực sinh trắc chi tiết.
* **Tệp ảnh hưởng:**
  * [`app/src/main/java/com/example/attendance/service/LocationService.java`](file:///d:/smart-attendence/app/src/main/java/com/example/attendance/service/LocationService.java)
  * [`app/src/main/java/com/example/attendance/service/FaceService.java`](file:///d:/smart-attendence/app/src/main/java/com/example/attendance/service/FaceService.java)
* **Đối soát với `docs/`:**
  * [x] Tuân thủ `01-SYSTEM_RULES` (Zero-Trash Data Policy & Biometric Quality Control).
  * [x] Đạt chuẩn `02-TECHNOLOGY_PROFILE` (Tích hợp Google MLKit Face Detection & Fused Location Provider).
  * [x] Khớp với `03-BUSINESS_MODULES` (`RULE_ATTEND_001` - Anti-spoofing Face & Fake GPS Protection).

### Change Record #3 — 02/10/2026 (Phase 3 Refactoring Execution)
* **Người thực hiện:** AI Assistant & Member 1
* **Mô tả thay đổi:** 
  1. **Cơ chế Offline Queue cho Heartbeat thất bại:** Nâng cấp `AttendanceMonitoringService.java` tự động chèn payload Heartbeat thất bại (mạng lag/mất kết nối 4G/Wi-Fi hoặc server lỗi) vào Room DB `PendingEventEntity`. `SyncWorker` sẽ đẩy bù dữ liệu khi thiết bị kết nối lại.
  2. **Tối ưu Doze Mode & Deep Sleep Resiliency:** Chuyển đổi cơ chế Timer từ `Handler.postDelayed()` sang `ScheduledExecutorService.scheduleAtFixedRate()` kết hợp `PowerManager.PARTIAL_WAKE_LOCK` ngắn (10s), đảm bảo service chạy ngầm định kỳ 3 phút mượt mà bất kể Doze Mode trên Android.
* **Tệp ảnh hưởng:**
  * [`app/src/main/java/com/example/attendance/service/AttendanceMonitoringService.java`](file:///d:/smart-attendence/app/src/main/java/com/example/attendance/service/AttendanceMonitoringService.java)
* **Đối soát với `docs/`:**
  * [x] Tuân thủ `01-SYSTEM_RULES` (Resilient Offline-First Strategy & Resiliency Policy).
  * [x] Đạt chuẩn `02-TECHNOLOGY_PROFILE` (Foreground Service Pattern & Room Persistence).
  * [x] Khớp với `03-BUSINESS_MODULES` (`RULE_HEARTBEAT_001` - Continuous Presence Monitoring).

---

## 6. History Log (Lịch Sử Chỉnh Sửa Dự Án)

| Ngày | Phiên bản | Tóm tắt thay đổi | Tình trạng đối soát `docs/` |
| :--- | :--- | :--- | :--- |
| **02/10/2026** | **v2.1.0** | Chuẩn hóa bộ 3 file kiến trúc `SYSTEM_RULES`, `TECH_PROFILE`, `BUSINESS_MODULES`. Hoàn thiện luồng 1-Chạm, Vé điện tử, Ra ngoài tạm thời `TEMP_OUT`, Zero-Trash Data Policy. | ✅ Đã đồng bộ 100% với `docs/` |
| **02/10/2026** | **v2.1.1** | **Phase 1 Refactoring:** Sửa lỗ hổng bypass GPS khi tắt vị trí, thêm thuật toán Haversine Distance, đối soát BSSID Wi-Fi và Dynamic Geofence Config. | ✅ Đã cập nhật & khớp 100% với `docs/` |
| **02/10/2026** | **v2.2.0** | **Phase 2 Refactoring:** Thuật toán Fake GPS 4 tầng (Teleportation Anomaly Check) & Kiểm tra góc xoay diện chẩn khuôn mặt Head Euler Pose Angle trong MLKit. | ✅ Đã cập nhật & khớp 100% với `docs/` |
| **02/10/2026** | **v2.3.0** | **Phase 3 Refactoring:** Tự động chèn Heartbeat thất bại vào Room DB Offline Queue & Tối ưu Doze Mode với ScheduledExecutorService & Partial WakeLock. | ✅ Đã cập nhật & khớp 100% với `docs/` |



