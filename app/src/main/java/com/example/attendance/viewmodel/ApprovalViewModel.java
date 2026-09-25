package com.example.attendance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.attendance.data.model.response.ApprovalHistoryItem;
import com.example.attendance.data.repository.ApprovalRepository;

import java.util.List;

public class ApprovalViewModel extends ViewModel {
    private final ApprovalRepository repository;

    private final MutableLiveData<List<ApprovalHistoryItem>> approvalHistoryList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ApprovalViewModel() {
        this.repository = new ApprovalRepository();
    }

    public LiveData<List<ApprovalHistoryItem>> getApprovalHistoryList() { return approvalHistoryList; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadApprovalHistory(String year, String month) {
        repository.getApprovalHistory(year, month, new ApprovalRepository.ApiCallback<List<ApprovalHistoryItem>>() {
            @Override
            public void onSuccess(List<ApprovalHistoryItem> result) {
                approvalHistoryList.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }
}
