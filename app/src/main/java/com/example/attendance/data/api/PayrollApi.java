package com.example.attendance.data.api;

import com.example.attendance.data.model.response.PayslipResponse;
import com.example.attendance.data.model.response.TimesheetResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PayrollApi {

    @GET("payroll/timesheet")
    Call<TimesheetResponse> getTimesheet(@Query("year") String year, @Query("month") String month);

    @GET("payroll/payslip")
    Call<PayslipResponse> getPayslip(@Query("year") String year, @Query("month") String month);
}
