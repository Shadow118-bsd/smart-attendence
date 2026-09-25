package com.example.attendance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.attendance.data.model.request.LeaveRequest;
import com.example.attendance.data.model.request.OTRequest;
import com.example.attendance.data.model.request.ResignRequest;
import com.example.attendance.data.model.response.RequestHistoryItem;
import com.example.attendance.data.repository.RequestRepository;

import java.util.List;

public class RequestViewModel extends ViewModel {
    private final RequestRepository repository;

    private final MutableLiveData<Boolean> submitResult = new MutableLiveData<>();
    private final MutableLiveData<List<RequestHistoryItem>> historyList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public RequestViewModel() {
        this.repository = new RequestRepository();
    }

    public LiveData<Boolean> getSubmitResult() { return submitResult; }
    public LiveData<List<RequestHistoryItem>> getHistoryList() { return historyList; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void submitLeave(LeaveRequest request) {
        repository.submitLeaveRequest(request, new RequestRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                submitResult.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void submitOT(OTRequest request) {
        repository.submitOTRequest(request, new RequestRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                submitResult.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void submitResign(ResignRequest request) {
        repository.submitResignRequest(request, new RequestRepository.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                submitResult.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void loadHistory(String year, String month) {
        repository.getRequestHistory(year, month, new RequestRepository.ApiCallback<List<RequestHistoryItem>>() {
            @Override
            public void onSuccess(List<RequestHistoryItem> result) {
                historyList.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }
}
