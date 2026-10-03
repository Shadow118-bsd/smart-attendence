# SYSTEM DEVELOPMENT RULES — SMART ATTENDANCE SYSTEM

> **Status:** Standardized Architecture Guideline  
> **Scope:** Technology-Independent & Business-Independent Engineering Rules  
> **Target Application:** Smart Attendance & Context-Aware Workforce System  
> **Version:** 2.1.0 (Standardized 3-Member Team Framework)

---

## 0. Tailoring & Sizing Framework (Phân Hạng Quy Mô Dự Án)

Hệ thống quy định 3 Cấp độ Tuân thủ (Compliance Levels) nhằm tránh nguy cơ **Over-engineering** cho dự án nhỏ nhưng vẫn bảo đảm tính mở rộng:

| Cấp độ (Level) | Quy mô & Bối cảnh | Mức độ Áp dụng Rules | Quy tắc Giản lược (Pruning Rules) |
| :--- | :--- | :--- | :--- |
| **Level 1: Lite** | • Dự án PoC, MVP, Hackathon<br>• Team 1 - 2 developers<br>• CRUD đơn giản | Basic Rules | • Gộp `Controller` và `Use Case` vào 1 handler.<br>• Bỏ tầng Repository độc lập.<br>• Gom cấu hình vào 1 file `PROJECT_RULES.md`. |
| **Level 2: Standard (Áp dụng)** | • Dự án sản phẩm tiêu chuẩn<br>• Team 3 thành viên (M1, M2, M3)<br>• Có Rule Engine & Phân tầng dữ liệu | Sections: 1 - 28, 33, 34 | • Bắt buộc tách 4 tầng Clean Architecture.<br>• Bắt buộc dùng Module Contract & Data Schemas.<br>• Bắt buộc duy trì bộ 3 file: `SYSTEM_RULES`, `TECH_PROFILE`, `BUSINESS_MODULES`. |
| **Level 3: Enterprise** | • Hệ thống Microservices / Multi-team<br>• > 10 developers<br>• Hàng ngàn Business Rules | Tất cả Sections (0 - 36) | • Bắt buộc cách ly Module Ownership, Contract Testing, ADR.<br>• Bắt buộc Event-Driven Queue, Saga Transaction & AI Resilience. |

---

## 1. Purpose & Scope

Tài liệu này định nghĩa bộ quy tắc kỹ thuật chung dùng để thiết kế, phát triển, kiểm thử, tích hợp và bảo trì hệ thống **Smart Attendance**.

* Các quy tắc trong file này **hoàn toàn độc lập với công nghệ** (không chứa thông tin thư viện cụ thể như React, Retrofit, Room DB, PostgreSQL,...).
* Các quy tắc **hoàn toàn độc lập với tên nghiệp vụ chi tiết** (công nghệ triển khai được quy định tại `02-TECHNOLOGY_PROFILE.md`, nghiệp vụ chi tiết quy định tại `03-BUSINESS_MODULES.md`).

---

## 2. Core Architecture Principles

1. **Separation of Concerns:** Tách biệt tuyệt đối giữa Giao diện người dùng (Presentation), Điều phối Use Case (Application), Logic Nghiệp vụ & Đánh giá Quy tắc (Domain & Rule Engine), và Tầng Hạ tầng (Infrastructure).
2. **Single Responsibility Principle:** Mỗi class/module/component chỉ chịu trách nhiệm cho một lý do thay đổi duy nhất.
3. **High Cohesion & Low Coupling:** Đạt độ gắn kết cao bên trong từng module và giảm thiểu tối đa sự phụ thuộc trực tiếp giữa các module của 3 thành viên.
4. **Dependency Direction:** Phụ thuộc luôn hướng từ ngoài vào trong:  
   $$\text{Presentation / Infra} \longrightarrow \text{Application} \longrightarrow \text{Domain / Rule Engine}$$
5. **Explicit Contracts:** Các module chỉ giao tiếp qua Contract (Interfaces/Schemas) công khai công bố, không truy cập vào internals của nhau.
6. **Validation Authority Rule:** Validation ở Frontend/Client chỉ phục vụ trải nghiệm người dùng (UX); Backend và Context Engine mới là cơ quan thẩm quyền cuối cùng (Authority) về Validation và Security.
7. **Zero-Trash Data Policy:** Mọi dữ liệu không đạt điều kiện xác thực tuyệt đối không được phép ghi vào CSDL hoặc gửi API rác làm ô nhiễm hệ thống.
8. **Resilient Offline-First Strategy:** Tầng Client phải hỗ trợ lưu trữ cục bộ và cơ chế đồng bộ tự động khi mất kết nối mạng.

---

## 3. System Architecture Model

Mô hình kiến trúc 4 tầng chuẩn:

```
┌───────────────────────────────────────────────────────────┐
│                    Presentation Layer                     │
│       (UI Activities, Fragments, Custom Views, Dialogs)   │
└─────────────────────────────┬─────────────────────────────┘
                              │
                              ▼
┌───────────────────────────────────────────────────────────┐
│                     Application Layer                     │
│         (ViewModels, Use Cases, Event Dispatchers)        │
└─────────────────────────────┬─────────────────────────────┘
                              │
                              ▼
┌───────────────────────────────────────────────────────────┐
│                Domain Layer & Rule Engine                 │
│      (Business Entities, Value Objects, Rule Evaluator)   │
└─────────────────────────────┬─────────────────────────────┘
                              │
                              ▼
┌───────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                   │
│ (Repositories Implementation, REST Clients, Services, DB) │
└───────────────────────────────────────────────────────────┘
```

---

## 4. Module Architecture & Directory Layout

### 4.1. Standard Directory Structure (Cấu trúc Thư mục Chuẩn)

Hệ thống quy định cấu trúc tổ chức mã nguồn theo định hướng **Modular Architecture (Package by Feature / Domain)**:

```
smart-attendance/
├── docs/                                  # Tài liệu kiến trúc & nghiệp vụ
│   ├── 01-SYSTEM_RULES.md
│   ├── 02-TECHNOLOGY_PROFILE.md
│   └── 03-BUSINESS_MODULES.md
│
├── app/src/main/java/com/example/attendance/
│   ├── data/                              # Data Contracts & Schemas
│   │   ├── model/
│   │   │   ├── request/                   # DTO Requests (CheckIn, Heartbeat, etc.)
│   │   │   └── response/                  # DTO Responses (ApiResponse, Ticket, etc.)
│   │   ├── local/                         # Room DB Entities & DAOs
│   │   └── repository/                    # Repository Interfaces & Impl
│   │
│   ├── ui/                                # Presentation Layer
│   │   ├── attendance/                    # Workflows: Check-in, Out, Electronic Ticket
│   │   ├── auth/                          # Authentication UI
│   │   ├── home/                          # Main Dashboard & 1-Touch Button
│   │   ├── profile/                       # User Profile UI
│   │   └── request/                       # Leave, OT, Resign Requests UI
│   │
│   ├── viewmodel/                         # Application Layer (ViewModels)
│   │   ├── AttendanceViewModel.java
│   │   ├── AuthViewModel.java
│   │   └── RequestViewModel.java
│   │
│   ├── service/                           # Infrastructure Services
│   │   ├── AttendanceMonitoringService.java # Foreground Heartbeat Monitoring Service
│   │   ├── LocationService.java           # GPS Hardware Provider
│   │   └── WifiService.java               # Wi-Fi BSSID/SSID Provider
│   │
│   ├── worker/                            # Background Sync WorkManager
│   │   ├── SyncWorker.java
│   │   └── HeartbeatWorker.java
│   │
│   └── utils/                             # Domain Verifiers & Helpers
│       ├── AttendanceVerifier.java
│       └── SessionManager.java
```

---

## 5. Layer Responsibilities & Boundaries

### 5.1. Presentation Layer (Tầng Giao diện)
* **Nhiệm vụ:** Hiển thị UI, tiếp nhận tương tác người dùng, hiển thị thông báo cảnh báo và Vé điểm danh điện tử.
* **Quy tắc:** Tuyệt đối **KHÔNG** chứa logic tính toán khoảng cách GPS hay xử lý gọi API trực tiếp. Chỉ quan sát (Observe) trạng thái từ ViewModel.

### 5.2. Application Layer (Tầng Ứng dụng / ViewModel)
* **Nhiệm vụ:** Quản lý trạng thái giao diện (UI State), điều phối dữ liệu giữa UI và Repository, kích hoạt các sự kiện kiểm tra.
* **Quy tắc:** Không giữ tham chiếu trực tiếp đến Android `Context` hay View elements để tránh memory leak.

### 5.3. Domain Layer & Rule Engine (Tầng Nghiệp vụ & Đánh giá Quy tắc)
* **Nhiệm vụ:** Định nghĩa các quy tắc cốt lõi (Xác thực 3 yếu tố Wi-Fi/GPS/Face, Zero-Trash Data Policy, Quy tắc Ra ngoài tạm thời).
* **Quy tắc:** Thuần túy logic, dễ dàng viết Unit Test độc lập với thiết bị Android.

### 5.4. Infrastructure Layer (Tầng Hạ tầng)
* **Nhiệm vụ:** Giao tiếp CSDL Room DB, gọi REST API qua Retrofit, thu thập tọa độ từ thiết bị GPS/Wi-Fi, chạy dịch vụ ngầm Foreground Service.
* **Quy tắc:** Bọc toàn bộ các ngoại lệ phần cứng/mạng thành các Domain Error chuẩn hóa.

---

## 6. Zero-Trash Data Policy & Error Handling Rules

1. **Nguyên tắc Chặn rác tại nguồn:**
   * Nếu dữ liệu GPS bị giả lập (Mock Location) hoặc Wi-Fi chưa kết nối: Chặn ngay tại bước thu thập, hủy tiến trình điểm danh.
   * Nếu khuôn mặt không vượt qua kiểm tra sinh trắc thực thể sống (Liveness): Xóa ngay lập tức file ảnh chụp tạm (`photoFile.delete()`), không chèn bản ghi rác vào Room DB.
2. **Hiển thị Cảnh báo Chuẩn đoán (Diagnostic Error Dialog):**
   * Mọi thất bại điểm danh phải hiển thị BottomSheet hướng dẫn nguyên nhân chi tiết kèm hành động khắc phục trực tiếp (Mở Cài đặt Wi-Fi, Cài đặt Vị trí, Chụp lại mặt).

---

## 7. Definition of Done (DoD)

### 7.1. Level 1: Development Done (Áp dụng từng Thành viên)
Một tính năng/module được coi là **Development Done** khi:
* [ ] Đã hoàn thiện **Module Contract (Payload DTO Schemas)** theo đúng thiết kế chung.
* [ ] Code chạy thành công với Mock Data / Local Data mà không đâm sầm (Crash).
* [ ] Giao diện hiển thị đúng layout, đủ các trạng thái `Loading`, `Success`, `Error`, `Empty`.
* [ ] Đã xử lý giải phóng tài nguyên (File ảnh rác, Location Listener, Service Receiver).

### 7.2. Level 2: Integration Done (Áp dụng Toàn nhóm M1 - M2 - M3)
Một module đạt **Integration Done** hoàn chỉnh khi:
* [ ] Kết nối thành công API giữa Client Android (M1), Backend (M2) và Context Engine (M3).
* [ ] Luồng điểm danh 1-Chạm hoạt động thông suốt từ Check-in ➔ Heartbeat 3 phút ➔ Check-out.
* [ ] Luồng Offline Sync hoạt động đúng khi mất mạng và có mạng trở lại.
* [ ] Trạng thái cảnh báo màu đỏ bật đúng khi nhân viên vượt ranh giới làm việc.
