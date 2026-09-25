package com.example.attendance.data.api;

import com.example.attendance.data.model.request.LeaveRequest;
import com.example.attendance.data.model.request.OTRequest;
import com.example.attendance.data.model.request.ResignRequest;
import com.example.attendance.data.model.response.RequestHistoryItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RequestApi {

    @POST("requests/leave")
    Call<Boolean> submitLeaveRequest(@Body LeaveRequest request);

    @POST("requests/overtime")
    Call<Boolean> submitOTRequest(@Body OTRequest request);

    @POST("requests/resign")
    Call<Boolean> submitResignRequest(@Body ResignRequest request);

    @GET("requests/history")
    Call<List<RequestHistoryItem>> getRequestHistory(@Query("year") String year, @Query("month") String month);

    @GET("requests/detail/{id}")
    Call<RequestHistoryItem> getRequestDetail(@Path("id") String requestId);

    @POST("requests/cancel/{id}")
    Call<Boolean> cancelRequest(@Path("id") String requestId);
}
