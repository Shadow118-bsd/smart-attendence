package com.example.attendance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.attendance.data.model.response.PayslipResponse;
import com.example.attendance.data.model.response.TimesheetResponse;
import com.example.attendance.data.repository.PayrollRepository;

public class PayrollViewModel extends ViewModel {
    private final PayrollRepository repository;

    private final MutableLiveData<TimesheetResponse> timesheetLiveData = new MutableLiveData<>();
    private final MutableLiveData<PayslipResponse> payslipLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public PayrollViewModel() {
        this.repository = new PayrollRepository();
    }

    public LiveData<TimesheetResponse> getTimesheetLiveData() { return timesheetLiveData; }
    public LiveData<PayslipResponse> getPayslipLiveData() { return payslipLiveData; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadTimesheet(String year, String month) {
        repository.getTimesheet(year, month, new PayrollRepository.ApiCallback<TimesheetResponse>() {
            @Override
            public void onSuccess(TimesheetResponse result) {
                timesheetLiveData.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void loadPayslip(String year, String month) {
        repository.getPayslip(year, month, new PayrollRepository.ApiCallback<PayslipResponse>() {
            @Override
            public void onSuccess(PayslipResponse result) {
                payslipLiveData.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }
}
