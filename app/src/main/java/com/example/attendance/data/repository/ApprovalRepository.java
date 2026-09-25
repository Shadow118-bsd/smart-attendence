package com.example.attendance.data.repository;

import com.example.attendance.data.api.ApprovalApi;
import com.example.attendance.data.api.RetrofitClient;
import com.example.attendance.data.model.response.ApprovalHistoryItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApprovalRepository {
    private final ApprovalApi approvalApi;

    public ApprovalRepository() {
        this.approvalApi = RetrofitClient.getClient().create(ApprovalApi.class);
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public void getApprovalHistory(String year, String month, ApiCallback<List<ApprovalHistoryItem>> callback) {
        approvalApi.getApprovalHistory(year, month).enqueue(new Callback<List<ApprovalHistoryItem>>() {
            @Override
            public void onResponse(Call<List<ApprovalHistoryItem>> call, Response<List<ApprovalHistoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải danh sách phê duyệt (Mã lỗi: " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<List<ApprovalHistoryItem>> call, Throwable t) {
                callback.onError("Không thể kết nối máy chủ: " + t.getMessage());
            }
        });
    }
}
