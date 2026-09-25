# Tài Liệu Bàn Giao & Hướng Dẫn Tích Hợp Module 1
## Android Employee App & Device Layer (Smart Attendance System)

---

## 1. Tổng Quan & Vai Trò Trong Kiến Trúc 3 Thành Viên

Hệ thống Điểm danh Thông minh (**Context-Aware Smart Attendance System**) được chia thành 3 Module tương ứng với 3 thành viên:

| Thành viên | Module | Vai trò & Trách nhiệm chính |
| :--- | :--- | :--- |
| **Member 1 (Module 1)** | **Android Employee App & Device Layer** | Giao diện người dùng (UI/UX), Thu thập bằng chứng (Face/MLKit, Wi-Fi BSSID/SSID, GPS Lat/Lng/Accuracy, Device ID), Giám sát ngầm (Foreground Service), Lưu trữ Offline (Room DB). |
| **Member 2** | **Backend, Database & Management** | Hệ thống REST API, Xác thực Auth (JWT/OAuth), Phân quyền RBAC, Quản lý CSDL (13 tables), Admin Dashboard & Báo cáo. |
| **Member 3** | **Context-Aware Attendance Engine** | Bộ máy phân tích trạng thái (State Machine: `PRESENT`, `TEMPORARILY_AWAY`, `OUT_OF_BOUNDS`, `WARNING`), Policy Engine, Tính toán thời gian làm việc & giờ OT. |

---

## 2. Quy Tắc Phân Tầng Dữ Liệu (4-Step Layered Data Flow)

Toàn bộ quy trình hoạt động giữa 3 thành viên tuân thủ nghiêm ngặt **Quy tắc phân tầng 4 bước**:

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
 │  Cập nhật UI/UX / Bật System Warning Notification màu đỏ  │
 └───────────────────────────────────────────────────────────┘
```

---

## 3. Hợp Đồng Dữ Liệu Evidence (Payload Specifications for Member 2 & 3)

Để **Member 2 (Backend)** nhận API và **Member 3 (Engine)** phân tích logic dễ dàng, **Module 1** đã chuẩn hóa tất cả các cấu trúc DTO Payload như sau:

### 3.1. Class `LocationEvidence` (Bằng chứng Vị trí)
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

### 3.2. Class `WifiEvidence` (Bằng chứng Mạng Wi-Fi)
```json
{
  "ssid": "FPT_Company_5G",
  "bssid": "00:1A:2B:3C:4D:5E",
  "ipAddress": "192.168.1.105",
  "isWifiConnected": true
}
```

### 3.3. Class `CheckInRequest` (Request Điểm danh Check-in)
```json
{
  "eventId": "e9b1a2c3-4d5e-6f7a-8b9c-0d1e2f3a4b5c",
  "timestamp": "2026-09-23T08:00:00+07:00",
  "deviceId": "9774d56d682e549c",
  "appVersion": "1.0.0",
  "face": {
    "livenessPassed": true,
    "faceImagePath": "/data/user/0/.../face_123.jpg",
    "faceVector": [0.12, -0.45, 0.88, ...]
  },
  "wifi": { ... },
  "location": { ... }
}
```

### 3.4. Class `HeartbeatRequest` (Request Giám sát Hiện diện)
```json
{
  "eventId": "f8a2b3c4-5d6e-7f8a-9b0c-1d2e3f4a5b6c",
  "sessionId": "SESSION_20260923_001",
  "timestamp": "2026-09-23T09:15:00+07:00",
  "deviceId": "9774d56d682e549c",
  "appVersion": "1.0.0",
  "wifi": { ... },
  "location": { ... }
}
```

---

## 4. Cơ Chế Giám Sát Ngầm & Cảnh Báo Của Module 1

Module 1 đã tích hợp sẵn dịch vụ chạy ngầm **`AttendanceMonitoringService`** (Android Foreground Service):

1. **Khởi chạy tự động**: Tự động kích hoạt khi người dùng **Check-in** thành công (nhận `sessionId` từ Server).
2. **Gửi Heartbeat định kỳ**: Mỗi **3 phút/lần**, thu thập Wi-Fi + GPS mới nhất và gửi về endpoint `POST /attendance/heartbeat`.
3. **Xử lý Warning từ Member 3 Engine**:
   - Nếu `HeartbeatResponse` chứa `status = "TEMPORARILY_AWAY"` hoặc `"OUT_OF_BOUNDS"` hoặc `"WARNING"`.
   - App lập tức kích hoạt **System Notification màu đỏ** có âm thanh cảnh báo nhân viên: *"Cảnh báo: Bạn đang ở ngoài khu vực làm việc/Wi-Fi công ty!"*.
4. **Dừng tự động**: Tự động hủy ngầm khi người dùng **Check-out** thành công.

---

## 5. Cơ Chế Lưu Trữ Offline & Tự Động Đồng Bộ (Room DB & WorkManager)

1. **Ngoại tuyến (No Internet)**:
   - Bản ghi điểm danh lập tức lưu vào CSDL cục bộ `AttendanceRecordEntity`.
   - Payload JSON được đẩy vào hàng đợi `PendingEventEntity` với trạng thái `PENDING`.
2. **Đồng bộ ngầm (Online Back)**:
   - `NetworkChangeReceiver` phát hiện có mạng trở lại và kích hoạt `SyncWorker` (Jetpack WorkManager).
   - `SyncWorker` duyệt các bản ghi `PENDING`, gửi tuần tự lên Server và cập nhật trạng thái `SYNCED`.

---

## 6. Danh Sách Endpoint REST API Module 1 Gọi Đến Member 2 Backend

| STT | HTTP Method | API Endpoint | Mục đích | DTO Request | DTO Response |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | `POST` | `/api/auth/login` | Đăng nhập tài khoản | `LoginRequest` | `LoginResponse` |
| 2 | `POST` | `/api/attendance/check-in` | Điểm danh vào ca | `CheckInRequest` | `CheckInResponse` |
| 3 | `POST` | `/api/attendance/check-out` | Điểm danh ra ca | `CheckOutRequest` | `CheckOutResponse` |
| 4 | `POST` | `/api/attendance/heartbeat` | Gửi bằng chứng hiện diện ngầm | `HeartbeatRequest` | `HeartbeatResponse` |
| 5 | `GET` | `/api/payroll/timesheet` | Lấy dữ liệu Bảng công | Query (`year`, `month`) | `TimesheetData` |
| 6 | `GET` | `/api/payroll/payslip` | Lấy dữ liệu Phiếu lương | Query (`year`, `month`) | `PayslipData` |
| 7 | `POST` | `/api/requests/leave` | Tạo đơn xin nghỉ phép | `LeaveRequest` | `ApiResponse<Void>` |
| 8 | `POST` | `/api/requests/overtime` | Tạo đơn làm thêm OT | `OTRequest` | `ApiResponse<Void>` |
| 9 | `POST` | `/api/requests/resign` | Tạo đề xuất thôi việc | `ResignRequest` | `ApiResponse<Void>` |

---

## 7. Hướng Dẫn Kế Thừa & Tích Hợp Cho Các Thành Viên Sau

### Dành cho Member 2 (Backend & DB):
- Xây dựng DB Tables chứa các trường dữ liệu khớp với Payload DTOs ở Mục 3 (đặc biệt các bảng `Attendance`, `Presence`, `Devices`).
- Đảm bảo endpoint `/api/attendance/check-in` trả về `sessionId` ngẫu nhiên duy nhất cho phiên làm việc.
- Bọc Response dạng `ApiResponse<T>` với các mã lỗi HTTP chuẩn (`200 OK`, `401 Unauthorized`, `403 Forbidden`, `400 Bad Request`).

### Dành cho Member 3 (Context Engine):
- Tiếp nhận Payload từ Member 2 khi có request `/attendance/heartbeat`.
- Đánh giá BSSID Wi-Fi và tọa độ GPS so với thiết lập `Workplace` trong DB.
- Trả về `status` (`PRESENT`, `TEMPORARILY_AWAY`, `OUT_OF_BOUNDS`, `WARNING`) trong `HeartbeatResponse` để Module 1 bật thông báo cảnh báo kịp thời cho nhân viên.

---

## 8. Cải Tiến Luồng Điểm Danh 1-Chạm & Vé Điểm Danh Điện Tử

Nhằm tối ưu hóa trải nghiệm người dùng daily (2-4 lần/ngày), **Module 1** đã nâng cấp toàn bộ luồng Check-in/Check-out:

1. **Dashboard Ca Làm Việc Thời Gian Thực (`HomeFragment`)**:
   - Thẻ `cardTodayAttendance` hiển thị ngay tại Trang chủ: Giờ vào, Giờ ra, Đồng hồ đếm thời gian làm việc thực tế và Huy hiệu vị trí hiện diện (`🟢 Trong vùng Wi-Fi`).
2. **Nút Điểm Danh Động 1-Chạm (`btnSmartAttendance`)**:
   - Tự động chuyển đổi dạng nút tùy thuộc vào trạng thái phiên làm việc:
     - Chưa Check-in: `[ 🟢 VÀO CA LÀM VIỆC (CHECK-IN) ]` (Màu xanh lá).
     - Đã Check-in: `[ 🟧 RA CA LÀM VIỆC (CHECK-OUT) ]` (Màu cam).
     - Đã Check-out: `[ 🔘 ĐÃ HOÀN THÀNH CA LÀM VIỆC ]` (Màu xám).
3. **Vé Điểm Danh Điện Tử (Electronic Attendance Ticket BottomSheet)**:
   - Khi hoàn tất xác thực, ứng dụng hiển thị chiếc vé điện tử dạng BottomSheet chứa: Ảnh chụp xác thực, Loại giao dịch (`CHECK-IN`/`CHECK-OUT`), Thời gian chính xác `dd/MM/yyyy HH:mm:ss`, Mạng Wi-Fi và Đánh giá ca (`ĐÚNG GIỜ`).

---

## 9. Tối Ưu Giao Diện & Loại Bỏ Thành Phần Dư Thừa

1. **Loại bỏ nút "Chấm công GPS"**:
   - Gỡ bỏ hoàn toàn nút `btnGridGps` khỏi lưới tính năng Màn hình chính. Toàn bộ điểm danh đã đi qua luồng Hybrid 1-Chạm Thông Minh (vừa chụp mặt, vừa lấy GPS & Wi-Fi tự động). Điều này giúp triệt tiêu hoàn toàn nguy cơ gian lận qua ứng dụng Fake GPS.
2. **Loại bỏ Activity Trung Gian**:
   - Bỏ màn hình chuyển tiếp `AttendanceActivity`. Khi `FaceVerificationActivity` chụp mặt xong, thực thi lưu DB/API ngay tại chỗ và bật chiếc vé BottomSheet trực tiếp.
3. **Cân bằng Grid Tính năng 6-Ô (2 hàng x 3 cột)**:
   - Tái cấu trúc GridLayout tại Trang chủ về dạng 6 tính năng chuẩn tỷ lệ: Đăng ký, Phê duyệt, Bảng công lương, Nhật ký In/Out, Tin tức, Khảo sát.

---

## 10. Quy Tắc Kiểm Soát Bằng Chứng Nâng Cao (Zero-Trash Data Policy) & Dialog Cảnh Báo Chuẩn Đoán

Để tránh quá tải dữ liệu (database pollution) và đảm bảo tính chính xác 100% cho hệ thống điểm danh, Module 1 áp dụng chính sách **Zero-Trash Data Policy** cùng cơ chế cảnh báo trực quan:

1. **Nguyên tắc Xác thực 3 Yếu tố (3-Factor Verification)**:
   - Một giao dịch điểm danh chỉ được chấp nhận khi đáp ứng đồng thời 100% cả 3 điều kiện:
     - Wi-Fi: Đã kết nối đúng mạng Wi-Fi công ty (SSID: `FPT_Office_5G`).
     - GPS: Vị trí thực tế nằm trong bán kính cho phép (<= 1000m) và không sử dụng phần mềm giả lập vị trí (Fake GPS / Mock Location).
     - FaceID & Liveness: Khuôn mặt trùng khớp với dữ liệu đăng ký và vượt qua kiểm tra sinh trắc học thực thể sống (Liveness).

2. **Quy trình Xử lý khi Không Đạt (Failure Handling)**:
   - Wi-Fi chưa bật/sai mạng: Hệ thống chặn ngay từ bước chụp ảnh, hiển thị BottomSheet hướng dẫn và nút mở nhanh Cài đặt Wi-Fi.
   - GPS không đạt / Mock GPS: Chặn điểm danh, thông báo nguyên nhân chi tiết và khoảng cách thực tế, cung cấp lối tắt mở Cài đặt Vị trí.
   - Nhắm mắt / Ảnh tĩnh / Không khớp mặt: Ngay lập tức xóa tệp ảnh tạm (`photoFile.delete()`), không ghi nhận bản ghi vào Room DB (`AttendanceRecordEntity`) và không gửi request lên Server API.

3. **Giao diện BottomSheet Cảnh Báo Chuẩn Đoán (`dialog_attendance_error.xml`)**:
   - Tiêu đề cảnh báo rõ ràng.
   - Khung hiển thị chi tiết Nguyên nhân thất bại (Reason) và Hướng dẫn khắc phục (Guidance).
   - Nút thao tác trực tiếp: "Thử lại" / "Chụp lại" hoặc "Cài đặt Wi-Fi" / "Cài đặt GPS".

---

## 11. Luồng Ra Ngoài Tạm Thời (Temporary Exit) & Quay Lại Ca Làm Việc (Mid-Shift Return)

Để đáp ứng các tình huống khẩn cấp giữa ca (sửa xe, gặp khách hàng đột xuất, việc gia đình khẩn) mà không làm ngắt ngắt phiên làm việc chính (`sessionId`), Module 1 đã thiết kế riêng luồng **Ra Ngoài Tạm Thời**:

1. **Phân biệt hai trạng thái**:
   - **Ra ngoài tạm thời (`TEMP_OUT`)**: Giữ nguyên phiên làm việc, chỉ báo tạm vắng kèm lý do. Nhân viên bấm nút `[ RA NGOÀI TẠM THỜI ]` -> Hiển thị BottomSheet `dialog_temporary_out.xml` để nhập lý do -> Chụp FaceID chính chủ. Wi-Fi được linh hoạt không bắt buộc 100% để tránh nghẽn khi bước ra ngoài cổng công ty.
   - **Quay lại làm việc (`TEMP_IN`)**: Nhân viên khi trở về văn phòng bấm `[ QUAY LẠI LÀM VIỆC ]`. Lúc này hệ thống **SIẾT CHẶT 100% CẢ 3 YẾU TỐ** (Wi-Fi công ty + GPS đúng bán kính + FaceID chính chủ) để chống gian lận giờ công.

2. **Chuyển đổi trạng thái giao diện Trang chủ (`HomeFragment`)**:
   - Chưa Check-in: Hiển thị nút `[ VÀO CA LÀM VIỆC (CHECK-IN) ]`.
   - Đang làm việc: Hiển thị nút chính `[ RA CA LÀM VIỆC (CHECK-OUT) ]` và nút phụ `[ RA NGOÀI TẠM THỜI ]`. Huy hiệu: "Trong vùng Wi-Fi".
   - Đang ra ngoài tạm thời: Nút phụ đổi thành `[ QUAY LẠI LÀM VIỆC ]`. Huy hiệu: "Ra ngoài tạm thời".
   - Đã Check-out: Nút `[ ĐÃ HOÀN THÀNH CA LÀM VIỆC ]`.

3. **Lưu trữ & Đối soát minh bạch**:
   - Các bản ghi `TEMP_OUT` và `TEMP_IN` được lưu vào CSDL Room DB (`AttendanceRecordEntity`) và đẩy lên Server để Quản lý (Manager) hậu kiểm tính minh bạch của ca làm việc.