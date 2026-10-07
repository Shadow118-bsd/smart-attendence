package com.example.attendance.data.repository;

import com.example.attendance.data.api.RequestApi;
import com.example.attendance.data.api.RetrofitClient;
import com.example.attendance.data.model.request.LeaveRequest;
import com.example.attendance.data.model.request.OTRequest;
import com.example.attendance.data.model.request.ResignRequest;
import com.example.attendance.data.model.response.RequestHistoryItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestRepository {
    private final RequestApi requestApi;
    private static final List<RequestHistoryItem> localRequestHistory = new ArrayList<>();

    static {
        localRequestHistory.add(new RequestHistoryItem(
                "REQ001",
                "[P] Nghỉ phép năm",
                "01/10/2026 - 02/10/2026",
                "Giải quyết việc gia đình",
                "Chờ duyệt",
                "01/10/2026"
        ));
        localRequestHistory.add(new RequestHistoryItem(
                "REQ002",
                "[OT] Làm thêm ngoài giờ",
                "15/09/2026 (17:30 - 20:30)",
                "Trực dự án triển khai",
                "Đã duyệt",
                "15/09/2026"
        ));
    }

    public RequestRepository() {
        this.requestApi = RetrofitClient.getClient().create(RequestApi.class);
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public void submitLeaveRequest(LeaveRequest request, ApiCallback<Boolean> callback) {
        requestApi.submitLeaveRequest(request).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addLocalLeave(request);
                    callback.onSuccess(response.body());
                } else {
                    addLocalLeave(request);
                    callback.onSuccess(true);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                addLocalLeave(request);
                callback.onSuccess(true);
            }
        });
    }

    private void addLocalLeave(LeaveRequest request) {
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        localRequestHistory.add(0, new RequestHistoryItem(
                "REQ_" + System.currentTimeMillis(),
                request.getLeaveType() != null ? request.getLeaveType() : "Nghỉ phép",
                request.getFromDate() + " - " + request.getToDate(),
                request.getReason() != null ? request.getReason() : "",
                "Chờ duyệt",
                today
        ));
    }

    public void submitOTRequest(OTRequest request, ApiCallback<Boolean> callback) {
        requestApi.submitOTRequest(request).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addLocalOT(request);
                    callback.onSuccess(response.body());
                } else {
                    addLocalOT(request);
                    callback.onSuccess(true);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                addLocalOT(request);
                callback.onSuccess(true);
            }
        });
    }

    private void addLocalOT(OTRequest request) {
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        localRequestHistory.add(0, new RequestHistoryItem(
                "REQ_" + System.currentTimeMillis(),
                "Đăng ký làm thêm (OT)",
                request.getOtDate() + " (" + request.getOtShift() + ")",
                request.getReason() != null ? request.getReason() : "",
                "Chờ duyệt",
                today
        ));
    }

    public void submitResignRequest(ResignRequest request, ApiCallback<Boolean> callback) {
        requestApi.submitResignRequest(request).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addLocalResign(request);
                    callback.onSuccess(response.body());
                } else {
                    addLocalResign(request);
                    callback.onSuccess(true);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                addLocalResign(request);
                callback.onSuccess(true);
            }
        });
    }

    private void addLocalResign(ResignRequest request) {
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        localRequestHistory.add(0, new RequestHistoryItem(
                "REQ_" + System.currentTimeMillis(),
                "Đề xuất thôi việc",
                "Ngày thôi việc dự kiến: " + request.getResignDate(),
                request.getReason() != null ? request.getReason() : "",
                "Chờ duyệt",
                today
        ));
    }

    public void getRequestHistory(String year, String month, ApiCallback<List<RequestHistoryItem>> callback) {
        requestApi.getRequestHistory(year, month).enqueue(new Callback<List<RequestHistoryItem>>() {
            @Override
            public void onResponse(Call<List<RequestHistoryItem>> call, Response<List<RequestHistoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(new ArrayList<>(localRequestHistory));
                }
            }

            @Override
            public void onFailure(Call<List<RequestHistoryItem>> call, Throwable t) {
                callback.onSuccess(new ArrayList<>(localRequestHistory));
            }
        });
    }
}
