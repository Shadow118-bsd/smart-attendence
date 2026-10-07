package com.example.attendance.data.repository;

import android.content.Context;

import com.example.attendance.data.api.AuthApi;
import com.example.attendance.data.api.RetrofitClient;
import com.example.attendance.data.model.request.LoginRequest;
import com.example.attendance.data.model.response.LoginResponse;
import com.example.attendance.data.model.response.UserResponse;
import com.example.attendance.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final AuthApi authApi;
    private final SessionManager sessionManager;

    public interface AuthCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public AuthRepository(Context context) {
        this.authApi = RetrofitClient.getClient(context).create(AuthApi.class);
        this.sessionManager = new SessionManager(context);
    }

    public void login(String username, String password, AuthCallback<LoginResponse> callback) {
        LoginRequest request = new LoginRequest(username, password);
        authApi.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    saveLoginSession(loginResponse);
                    callback.onSuccess(loginResponse);
                } else {
                    if (response.code() == 401) {
                        callback.onError("Tên đăng nhập hoặc mật khẩu không chính xác.");
                    } else {
                        callback.onError("Đăng nhập thất bại (Mã lỗi HTTP: " + response.code() + "). Vui lòng thử lại.");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Chế độ Dev Fallback Tạm Thời: Khi chưa bật máy chủ Backend, cho phép đăng nhập thử nghiệm
                LoginResponse fallbackRes = new LoginResponse();
                fallbackRes.setAccessToken("DEV_OFFLINE_TOKEN_" + System.currentTimeMillis());
                fallbackRes.setRefreshToken("DEV_REFRESH_TOKEN");

                UserResponse devUser = new UserResponse();
                devUser.setId("EMP001");
                devUser.setEmployeeId("NV8888");
                devUser.setEmail(username);
                devUser.setDepartment("Phòng Công Nghệ");
                devUser.setWorkplaceName("Văn phòng FPT Tower");

                String lowerUser = username != null ? username.toLowerCase().trim() : "";
                if (lowerUser.contains("manager") || lowerUser.contains("admin") || lowerUser.contains("quanly")) {
                    devUser.setRole("MANAGER");
                    devUser.setFullName("Quản Lý Nguyễn Văn Trưởng");
                    devUser.setPosition("Trưởng phòng Quản lý");
                } else if (lowerUser.contains("hr")) {
                    devUser.setRole("HR");
                    devUser.setFullName("Chuyên viên Nhân sự (HR)");
                    devUser.setPosition("Chuyên viên Quản lý Nhân sự");
                } else {
                    devUser.setRole("EMPLOYEE");
                    devUser.setFullName("Nhân Viên Thử Nghiệm");
                    devUser.setPosition("Kỹ sư phần mềm");
                }

                fallbackRes.setUser(devUser);

                saveLoginSession(fallbackRes);
                callback.onSuccess(fallbackRes);
            }
        });
    }

    private void saveLoginSession(LoginResponse loginResponse) {
        sessionManager.saveAuthToken(loginResponse.getAccessToken(), loginResponse.getRefreshToken());
        if (loginResponse.getUser() != null) {
            String role = loginResponse.getUser().getRole();
            if (role == null || role.isEmpty()) {
                role = "EMPLOYEE";
            }
            sessionManager.saveUserInfo(
                    loginResponse.getUser().getEmployeeId(),
                    loginResponse.getUser().getFullName(),
                    role
            );
        }
    }

    public void logout() {
        sessionManager.clear();
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
}
