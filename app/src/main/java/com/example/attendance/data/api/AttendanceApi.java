package com.example.attendance.data.api;

import com.example.attendance.data.model.request.CheckInRequest;
import com.example.attendance.data.model.request.CheckOutRequest;
import com.example.attendance.data.model.request.HeartbeatRequest;
import com.example.attendance.data.model.response.CheckInResponse;
import com.example.attendance.data.model.response.CheckOutResponse;
import com.example.attendance.data.model.response.HeartbeatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AttendanceApi {

    @POST("attendance/check-in")
    Call<CheckInResponse> checkIn(@Body CheckInRequest request);

    @POST("attendance/check-out")
    Call<CheckOutResponse> checkOut(@Body CheckOutRequest request);

    @POST("attendance/heartbeat")
    Call<HeartbeatResponse> heartbeat(@Body HeartbeatRequest request);
}
