package com.example.attendance.data.api;

import com.example.attendance.data.model.request.LoginRequest;
import com.example.attendance.data.model.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/logout")
    Call<Void> logout();
}
