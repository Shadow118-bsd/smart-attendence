package com.example.attendance.data.repository;

import com.example.attendance.data.api.RequestApi;
import com.example.attendance.data.api.RetrofitClient;
import com.example.attendance.data.model.request.LeaveRequest;
import com.example.attendance.data.model.request.OTRequest;
import com.example.attendance.data.model.request.ResignRequest;
import com.example.attendance.data.model.response.RequestHistoryItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestRepository {
    private final RequestApi requestApi;

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
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Gửi yêu cầu nghỉ phép thất bại (Mã lỗi: " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                callback.onError("Không thể kết nối máy chủ: " + t.getMessage());
            }
        });
    }

    public void submitOTRequest(OTRequest request, ApiCallback<Boolean> callback) {
        requestApi.submitOTRequest(request).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Gửi yêu cầu làm thêm giờ thất bại (Mã lỗi: " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                callback.onError("Không thể kết nối máy chủ: " + t.getMessage());
            }
        });
    }

    public void submitResignRequest(ResignRequest request, ApiCallback<Boolean> callback) {
        requestApi.submitResignRequest(request).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Gửi yêu cầu thôi việc thất bại (Mã lỗi: " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                callback.onError("Không thể kết nối máy chủ: " + t.getMessage());
            }
        });
    }

    public void getRequestHistory(String year, String month, ApiCallback<List<RequestHistoryItem>> callback) {
        requestApi.getRequestHistory(year, month).enqueue(new Callback<List<RequestHistoryItem>>() {
            @Override
            public void onResponse(Call<List<RequestHistoryItem>> call, Response<List<RequestHistoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải lịch sử yêu cầu (Mã lỗi: " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<List<RequestHistoryItem>> call, Throwable t) {
                callback.onError("Không thể kết nối máy chủ: " + t.getMessage());
            }
        });
    }
}
