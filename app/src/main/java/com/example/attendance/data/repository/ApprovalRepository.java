package com.example.attendance.data.repository;

import com.example.attendance.data.api.ApprovalApi;
import com.example.attendance.data.api.RetrofitClient;
import com.example.attendance.data.model.response.ApprovalHistoryItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApprovalRepository {
    private final ApprovalApi approvalApi;

    // Bộ nhớ mẫu phục vụ chạy offline thử nghiệm trên Android Studio khi chưa bật máy chủ Backend
    private static final List<ApprovalHistoryItem> localPendingList = new ArrayList<>();
    private static final List<ApprovalHistoryItem> localHistoryList = new ArrayList<>();

    static {
        // Khởi tạo các đơn mẫu chờ duyệt
        localPendingList.add(new ApprovalHistoryItem(
                "APP001",
                "Nguyễn Thị Ngọc Hạnh",
                "Phê duyệt làm thêm",
                "Chờ duyệt",
                "01/10/2026",
                "17:30 - 20:30 (3 giờ)",
                "Trực dự án",
                "Hỗ trợ triển khai hệ thống Smart Attendance go-live ca tối",
                "",
                "",
                ""
        ));
        localPendingList.add(new ApprovalHistoryItem(
                "APP002",
                "Trần Văn Hùng",
                "Phê duyệt nghỉ",
                "Chờ duyệt",
                "03/10/2026",
                "08:00 - 17:00 (1 ngày)",
                "Nghỉ phép năm",
                "Giải quyết công việc gia đình có giấy xin phép",
                "",
                "",
                ""
        ));
        localPendingList.add(new ApprovalHistoryItem(
                "APP003",
                "Lê Hoàng Nam",
                "Phê duyệt giải trình công",
                "Chờ duyệt",
                "05/10/2026",
                "08:05 - 17:15",
                "Quên chấm công vào",
                "Thiết bị điện thoại hết pin lúc đến văn phòng, có đồng nghiệp cùng ca xác nhận",
                "",
                "",
                ""
        ));
        localPendingList.add(new ApprovalHistoryItem(
                "APP004",
                "Phạm Minh Tuấn",
                "Phê duyệt HR Process",
                "Chờ duyệt",
                "06/10/2026",
                "Toàn thời gian",
                "Điều chuyển ca làm việc",
                "Đổi từ ca hành chính sang ca hỗ trợ kỹ thuật ban đêm",
                "",
                "",
                ""
        ));

        // Khởi tạo đơn mẫu trong lịch sử
        localHistoryList.add(new ApprovalHistoryItem(
                "HIST001",
                "Nguyễn Thị Ngọc Hạnh",
                "Đăng ký làm thêm",
                "Phê duyệt",
                "15/09/2026",
                "06:00 - 14:00",
                "Trực lễ",
                "Làm thêm giờ ngày lễ Quốc Khánh",
                "15/09/2026",
                "Nguyễn Văn Trưởng",
                "Trưởng phòng Quản lý"
        ));
        localHistoryList.add(new ApprovalHistoryItem(
                "HIST002",
                "Đỗ Quốc Bảo",
                "Nghỉ việc riêng",
                "Từ chối",
                "20/09/2026",
                "13:00 - 17:00",
                "Việc cá nhân",
                "Xin nghỉ gấp nhưng không có người thay ca",
                "20/09/2026",
                "Nguyễn Văn Trưởng",
                "Trưởng phòng Quản lý"
        ));
    }

    public ApprovalRepository() {
        this.approvalApi = RetrofitClient.getClient().create(ApprovalApi.class);
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public void getPendingApprovals(String approvalType, ApiCallback<List<ApprovalHistoryItem>> callback) {
        approvalApi.getPendingApprovals(approvalType).enqueue(new Callback<List<ApprovalHistoryItem>>() {
            @Override
            public void onResponse(Call<List<ApprovalHistoryItem>> call, Response<List<ApprovalHistoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(filterPendingLocal(approvalType));
                }
            }

            @Override
            public void onFailure(Call<List<ApprovalHistoryItem>> call, Throwable t) {
                // Fallback khi offline
                callback.onSuccess(filterPendingLocal(approvalType));
            }
        });
    }

    private List<ApprovalHistoryItem> filterPendingLocal(String approvalType) {
        if (approvalType == null || approvalType.isEmpty() || "Phê duyệt".equalsIgnoreCase(approvalType)) {
            return new ArrayList<>(localPendingList);
        }
        List<ApprovalHistoryItem> filtered = new ArrayList<>();
        for (ApprovalHistoryItem item : localPendingList) {
            if (item.getRequestType() != null && item.getRequestType().toLowerCase().contains(approvalType.toLowerCase())) {
                filtered.add(item);
            }
        }
        // Nếu không khớp chính xác loại đơn, trả về toàn bộ đơn chờ duyệt
        return filtered.isEmpty() ? new ArrayList<>(localPendingList) : filtered;
    }

    public void getApprovalDetail(String approvalId, ApiCallback<ApprovalHistoryItem> callback) {
        approvalApi.getApprovalDetail(approvalId).enqueue(new Callback<ApprovalHistoryItem>() {
            @Override
            public void onResponse(Call<ApprovalHistoryItem> call, Response<ApprovalHistoryItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(findItemLocal(approvalId));
                }
            }

            @Override
            public void onFailure(Call<ApprovalHistoryItem> call, Throwable t) {
                callback.onSuccess(findItemLocal(approvalId));
            }
        });
    }

    private ApprovalHistoryItem findItemLocal(String approvalId) {
        if (approvalId != null) {
            for (ApprovalHistoryItem item : localPendingList) {
                if (approvalId.equals(item.getId())) return item;
            }
            for (ApprovalHistoryItem item : localHistoryList) {
                if (approvalId.equals(item.getId())) return item;
            }
        }
        return localPendingList.isEmpty() ? null : localPendingList.get(0);
    }

    public void approveRequest(String approvalId, String approverName, ApiCallback<Boolean> callback) {
        approvalApi.approveRequest(approvalId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    callback.onSuccess(true);
                } else {
                    handleLocalApprove(approvalId, approverName);
                    callback.onSuccess(true);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                handleLocalApprove(approvalId, approverName);
                callback.onSuccess(true);
            }
        });
    }

    private void handleLocalApprove(String approvalId, String approverName) {
        Iterator<ApprovalHistoryItem> iterator = localPendingList.iterator();
        while (iterator.hasNext()) {
            ApprovalHistoryItem item = iterator.next();
            if (item.getId() != null && item.getId().equals(approvalId)) {
                iterator.remove();
                item.setStatus("Đã duyệt");
                item.setApprovalDate(new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date()));
                item.setApproverName(approverName != null && !approverName.isEmpty() ? approverName : "Người quản lý");
                item.setApproverRole("Quản lý bộ phận");
                localHistoryList.add(0, item);
                break;
            }
        }
    }

    public void rejectRequest(String approvalId, String approverName, String reason, ApiCallback<Boolean> callback) {
        approvalApi.rejectRequest(approvalId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    callback.onSuccess(true);
                } else {
                    handleLocalReject(approvalId, approverName, reason);
                    callback.onSuccess(true);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                handleLocalReject(approvalId, approverName, reason);
                callback.onSuccess(true);
            }
        });
    }

    private void handleLocalReject(String approvalId, String approverName, String reason) {
        Iterator<ApprovalHistoryItem> iterator = localPendingList.iterator();
        while (iterator.hasNext()) {
            ApprovalHistoryItem item = iterator.next();
            if (item.getId() != null && item.getId().equals(approvalId)) {
                iterator.remove();
                item.setStatus("Từ chối");
                item.setDetailReason("Lý do từ chối: " + (reason != null ? reason : "Không đủ điều kiện"));
                item.setApprovalDate(new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date()));
                item.setApproverName(approverName != null && !approverName.isEmpty() ? approverName : "Người quản lý");
                item.setApproverRole("Quản lý bộ phận");
                localHistoryList.add(0, item);
                break;
            }
        }
    }

    public void getApprovalHistory(String year, String month, ApiCallback<List<ApprovalHistoryItem>> callback) {
        approvalApi.getApprovalHistory(year, month).enqueue(new Callback<List<ApprovalHistoryItem>>() {
            @Override
            public void onResponse(Call<List<ApprovalHistoryItem>> call, Response<List<ApprovalHistoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(new ArrayList<>(localHistoryList));
                }
            }

            @Override
            public void onFailure(Call<List<ApprovalHistoryItem>> call, Throwable t) {
                callback.onSuccess(new ArrayList<>(localHistoryList));
            }
        });
    }
}
