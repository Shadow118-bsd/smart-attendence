package com.example.attendance.data.api;

import com.example.attendance.data.model.response.ApprovalHistoryItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApprovalApi {

    @GET("approvals/pending")
    Call<List<ApprovalHistoryItem>> getPendingApprovals(@Query("type") String approvalType);

    @GET("approvals/history")
    Call<List<ApprovalHistoryItem>> getApprovalHistory(@Query("year") String year, @Query("month") String month);

    @GET("approvals/detail/{id}")
    Call<ApprovalHistoryItem> getApprovalDetail(@Path("id") String approvalId);

    @POST("approvals/approve/{id}")
    Call<Boolean> approveRequest(@Path("id") String approvalId);

    @POST("approvals/reject/{id}")
    Call<Boolean> rejectRequest(@Path("id") String approvalId);
}
