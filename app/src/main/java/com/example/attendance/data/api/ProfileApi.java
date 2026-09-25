package com.example.attendance.data.api;

import com.example.attendance.data.model.response.DirectoryUserItem;
import com.example.attendance.data.model.response.NotificationItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProfileApi {

    @GET("profile/directory")
    Call<List<DirectoryUserItem>> getDirectoryList(@Query("query") String searchQuery);

    @GET("notifications")
    Call<List<NotificationItem>> getNotifications();

    @POST("notifications/read-all")
    Call<Boolean> markAllNotificationsRead();
}
