package com.example.attendance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.attendance.data.model.response.ApprovalHistoryItem;
import com.example.attendance.data.repository.ApprovalRepository;

import java.util.List;

public class ApprovalViewModel extends ViewModel {
    private final ApprovalRepository repository;

    private final MutableLiveData<List<ApprovalHistoryItem>> pendingApprovalList = new MutableLiveData<>();
    private final MutableLiveData<ApprovalHistoryItem> approvalDetail = new MutableLiveData<>();
    private final MutableLiveData<List<ApprovalHistoryItem>> approvalHistoryList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> actionSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ApprovalViewModel() {
        this.repository = new ApprovalRepository();
    }

    public LiveData<List<ApprovalHistoryItem>> getPendingApprovalList() { return pendingApprovalList; }
    public LiveData<ApprovalHistoryItem> getApprovalDetail() { return approvalDetail; }
    public LiveData<List<ApprovalHistoryItem>> getApprovalHistoryList() { return approvalHistoryList; }
    public LiveData<Boolean> getActionSuccess() { return actionSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadPendingApprovals(String approvalType) {
        isLoading.postValue(true);
        repository.getPendingApprovals(approvalType, new ApprovalRepository.ApiCallback<List<ApprovalHistoryItem>>() {
            @Override
            public void onSuccess(List<ApprovalHistoryItem> result) {
                isLoading.postValue(false);
                pendingApprovalList.postValue(result);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void loadApprovalDetail(String id) {
        isLoading.postValue(true);
        repository.getApprovalDetail(id, new ApprovalRepository.ApiCallback<ApprovalHistoryItem>() {
            @Override
            public void onSuccess(ApprovalHistoryItem result) {
                isLoading.postValue(false);
                approvalDetail.postValue(result);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void approveRequest(String id, String approverName) {
        isLoading.postValue(true);
        repository.approveRequest(id, approverName, new ApprovalRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isLoading.postValue(false);
                actionSuccess.postValue(result);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void rejectRequest(String id, String approverName, String reason) {
        isLoading.postValue(true);
        repository.rejectRequest(id, approverName, reason, new ApprovalRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isLoading.postValue(false);
                actionSuccess.postValue(result);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void loadApprovalHistory(String year, String month) {
        isLoading.postValue(true);
        repository.getApprovalHistory(year, month, new ApprovalRepository.ApiCallback<List<ApprovalHistoryItem>>() {
            @Override
            public void onSuccess(List<ApprovalHistoryItem> result) {
                isLoading.postValue(false);
                approvalHistoryList.postValue(result);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }
}
