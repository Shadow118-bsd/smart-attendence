package com.example.attendance.data.repository;

import com.example.attendance.data.api.ProfileApi;
import com.example.attendance.data.api.RetrofitClient;
import com.example.attendance.data.model.response.DirectoryUserItem;
import com.example.attendance.data.model.response.NotificationItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {
    private final ProfileApi profileApi;

    public ProfileRepository() {
        this.profileApi = RetrofitClient.getClient().create(ProfileApi.class);
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public void getDirectoryList(String query, ApiCallback<List<DirectoryUserItem>> callback) {
        profileApi.getDirectoryList(query).enqueue(new Callback<List<DirectoryUserItem>>() {
            @Override
            public void onResponse(Call<List<DirectoryUserItem>> call, Response<List<DirectoryUserItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải danh bạ nhân viên (Mã lỗi: " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<List<DirectoryUserItem>> call, Throwable t) {
                callback.onError("Không thể kết nối máy chủ: " + t.getMessage());
            }
        });
    }

    public void getNotifications(ApiCallback<List<NotificationItem>> callback) {
        profileApi.getNotifications().enqueue(new Callback<List<NotificationItem>>() {
            @Override
            public void onResponse(Call<List<NotificationItem>> call, Response<List<NotificationItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải danh sách thông báo (Mã lỗi: " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<List<NotificationItem>> call, Throwable t) {
                callback.onError("Không thể kết nối máy chủ: " + t.getMessage());
            }
        });
    }
}
