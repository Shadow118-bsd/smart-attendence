package com.example.attendance.data.repository;

import com.example.attendance.data.api.PayrollApi;
import com.example.attendance.data.api.RetrofitClient;
import com.example.attendance.data.model.response.PayslipResponse;
import com.example.attendance.data.model.response.TimesheetResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayrollRepository {
    private final PayrollApi payrollApi;

    public PayrollRepository() {
        this.payrollApi = RetrofitClient.getClient().create(PayrollApi.class);
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public void getTimesheet(String year, String month, ApiCallback<TimesheetResponse> callback) {
        payrollApi.getTimesheet(year, month).enqueue(new Callback<TimesheetResponse>() {
            @Override
            public void onResponse(Call<TimesheetResponse> call, Response<TimesheetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải bảng công (Mã lỗi: " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<TimesheetResponse> call, Throwable t) {
                callback.onError("Không thể kết nối máy chủ: " + t.getMessage());
            }
        });
    }

    public void getPayslip(String year, String month, ApiCallback<PayslipResponse> callback) {
        payrollApi.getPayslip(year, month).enqueue(new Callback<PayslipResponse>() {
            @Override
            public void onResponse(Call<PayslipResponse> call, Response<PayslipResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải phiếu lương (Mã lỗi: " + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<PayslipResponse> call, Throwable t) {
                callback.onError("Không thể kết nối máy chủ: " + t.getMessage());
            }
        });
    }
}
