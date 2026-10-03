# TECHNOLOGY PROFILE — SMART ATTENDANCE SYSTEM

> **Status:** Active Technology Binding  
> **Target System:** Android Client & Cross-Member Integration Stack  
> **Version:** 2.1.0

---

## 1. Stack Overview

Tài liệu này định nghĩa công nghệ cụ thể được sử dụng để triển khai các tiêu chuẩn trong [01-SYSTEM_RULES.md](file:///d:/smart-attendence/docs/01-SYSTEM_RULES.md).

```
┌───────────────────────────────────────────────────────────┐
│ PRESENTATION: Android Native Java + Material Design 3    │
│ (ConstraintLayout, ViewBinding, BottomSheetDialogFragment)│
├───────────────────────────────────────────────────────────┤
│ APPLICATION / STATE: Jetpack ViewModel + LiveData        │
├───────────────────────────────────────────────────────────┤
│ BIOMETRICS / AI: Google MLKit Face Detection (Local SDK)  │
├───────────────────────────────────────────────────────────┤
│ NETWORKING: Retrofit 2 + Gson + OkHttp 4                  │
├───────────────────────────────────────────────────────────┤
│ LOCAL STORAGE: Room Persistence Library (SQLite ORM)      │
├───────────────────────────────────────────────────────────┤
│ BACKGROUND SERVICES: Foreground Service + WorkManager    │
└───────────────────────────────────────────────────────────┘
```

---

## 2. Technology Mapping to Architecture Layers

| Architectural Layer (`SYSTEM_RULES`) | Technology Choice | Package / Tool |
| :--- | :--- | :--- |
| **UI & Presentation** | Android Native (Java 17) | `androidx.appcompat`, `com.google.android.material` |
| **State Management** | Android Jetpack Lifecycle | `androidx.lifecycle.ViewModel`, `LiveData` |
| **Networking Client** | Retrofit 2 & OkHttp | `com.squareup.retrofit2:retrofit`, `converter-gson` |
| **Local Database & Cache** | Room Database | `androidx.room:room-runtime`, `room-compiler` |
| **Foreground Monitoring** | Android Service | `android.app.Service` (Foreground with Notification) |
| **Offline Synchronization** | Jetpack WorkManager | `androidx.work:work-runtime` |
| **Face Recognition & Liveness** | Google MLKit Face Detection | `com.google.mlkit:face-detection` |
| **Hardware Providers** | Fused Location & WifiManager | `com.google.android.gms.location`, `WifiManager` |

---

## 3. Technology Standards & Coding Conventions

### 3.1. Java Code Naming Conventions
* **Package Names:** Lowercase (`com.example.attendance.ui.attendance`, `com.example.attendance.data.model`)
* **Classes & Interfaces:** `PascalCase` (`AttendanceActivity`, `AttendanceRepository`, `WifiEvidence`)
* **Variables & Methods:** `camelCase` (`isWifiConnected`, `getCurrentLocation()`)
* **Constants & Enums:** `UPPER_SNAKE_CASE` (`MAX_DISTANCE_METERS`, `STATUS_PRESENT`)
* **Layout Files:** `activity_*.xml`, `fragment_*.xml`, `dialog_*.xml`, `item_*.xml`

### 3.2. Architecture Implementation Patterns

#### A. Presentation & ViewModel Pattern (MVVM)
```java
// Controller/Activity chỉ lắng nghe LiveData và cập nhật UI
public class AttendanceActivity extends AppCompatActivity {
    private AttendanceViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);
        
        viewModel.getAttendanceStatus().observe(this, status -> {
            updateAttendanceUI(status);
        });
    }
}
```

#### B. Offline Synchronization Pattern (Room & WorkManager)
```java
// SyncWorker duyệt hàng đợi PendingEventEntity và gửi tuần tự lên Server
public class SyncWorker extends Worker {
    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Fetch PENDING records from Room DB and sync via Retrofit
        return Result.success();
    }
}
```

#### C. Foreground Service Pattern (Continuous Monitoring)
```java
// AttendanceMonitoringService chạy ngầm định kỳ 3 phút gửi Heartbeat
public class AttendanceMonitoringService extends Service {
    private static final long HEARTBEAT_INTERVAL = 3 * 60 * 1000; // 3 phút

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        scheduleHeartbeat();
        return START_STICKY;
    }
}
```

---

## 4. Environment & Configuration

Configurations được quản lý tập trung qua `gradle.properties` và `SessionManager`:

```properties
# Base REST API Gateway (Member 2 Server)
BASE_URL=https://api.smartattendance.example.com/v1/

# Heartbeat & Monitoring Settings
HEARTBEAT_INTERVAL_MINUTES=3
MAX_GEOFENCE_RADIUS_METERS=1000
```
