package com.example.attendance.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.attendance.data.model.request.FaceEvidence;
import com.example.attendance.data.model.request.LocationEvidence;
import com.example.attendance.data.model.response.CheckInResponse;
import com.example.attendance.data.model.response.CheckOutResponse;
import com.example.attendance.data.repository.AttendanceRepository;
import com.example.attendance.service.LocationService;
import com.example.attendance.utils.Resource;

public class AttendanceViewModel extends AndroidViewModel {
    private final AttendanceRepository repository;
    private final LocationService locationService;

    private final MutableLiveData<Resource<CheckInResponse>> checkInState = new MutableLiveData<>();
    private final MutableLiveData<Resource<CheckOutResponse>> checkOutState = new MutableLiveData<>();

    private final MutableLiveData<CheckInResponse> checkInResult = new MutableLiveData<>();
    private final MutableLiveData<CheckOutResponse> checkOutResult = new MutableLiveData<>();
    private final MutableLiveData<String> offlineNotice = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AttendanceViewModel(@NonNull Application application) {
        super(application);
        this.repository = new AttendanceRepository(application);
        this.locationService = new LocationService(application);
    }

    public LiveData<Resource<CheckInResponse>> getCheckInState() { return checkInState; }
    public LiveData<Resource<CheckOutResponse>> getCheckOutState() { return checkOutState; }

    public LiveData<CheckInResponse> getCheckInResult() { return checkInResult; }
    public LiveData<CheckOutResponse> getCheckOutResult() { return checkOutResult; }
    public LiveData<String> getOfflineNotice() { return offlineNotice; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void checkIn(FaceEvidence faceEvidence, String faceImagePath) {
        isLoading.setValue(true);
        checkInState.setValue(Resource.loading());

        locationService.getCurrentLocation(locationEvidence -> {
            if (locationEvidence != null && locationEvidence.isMock()) {
                isLoading.setValue(false);
                String mockError = "Phát hiện vị trí giả lập (Fake GPS). Vui lòng tắt ứng dụng tạo vị trí giả để điểm danh.";
                errorMessage.setValue(mockError);
                checkInState.setValue(Resource.error(mockError));
                return;
            }

            repository.checkIn(faceEvidence, locationEvidence, faceImagePath, new AttendanceRepository.AttendanceCallback<CheckInResponse>() {
                @Override
                public void onSuccess(CheckInResponse result) {
                    isLoading.setValue(false);
                    checkInResult.setValue(result);
                    checkInState.setValue(Resource.success(result));
                }

                @Override
                public void onError(String message) {
                    isLoading.setValue(false);
                    errorMessage.setValue(message);
                    checkInState.setValue(Resource.error(message));
                }

                @Override
                public void onSavedOffline(String eventId) {
                    isLoading.setValue(false);
                    String notice = "Mất kết nối Internet. Đã lưu dữ liệu Check-in ngoại tuyến và sẽ tự động đồng bộ khi có mạng!";
                    offlineNotice.setValue(notice);
                    checkInState.setValue(Resource.offlineSaved(notice));
                }
            });
        });
    }

    public void checkOut() {
        isLoading.setValue(true);
        checkOutState.setValue(Resource.loading());

        locationService.getCurrentLocation(locationEvidence -> {
            if (locationEvidence != null && locationEvidence.isMock()) {
                isLoading.setValue(false);
                String mockError = "Phát hiện vị trí giả lập (Fake GPS). Vui lòng tắt ứng dụng tạo vị trí giả để điểm danh.";
                errorMessage.setValue(mockError);
                checkOutState.setValue(Resource.error(mockError));
                return;
            }

            repository.checkOut(locationEvidence, new AttendanceRepository.AttendanceCallback<CheckOutResponse>() {
                @Override
                public void onSuccess(CheckOutResponse result) {
                    isLoading.setValue(false);
                    checkOutResult.setValue(result);
                    checkOutState.setValue(Resource.success(result));
                }

                @Override
                public void onError(String message) {
                    isLoading.setValue(false);
                    errorMessage.setValue(message);
                    checkOutState.setValue(Resource.error(message));
                }

                @Override
                public void onSavedOffline(String eventId) {
                    isLoading.setValue(false);
                    String notice = "Mất kết nối Internet. Đã lưu dữ liệu Check-out ngoại tuyến và sẽ tự động đồng bộ khi có mạng!";
                    offlineNotice.setValue(notice);
                    checkOutState.setValue(Resource.offlineSaved(notice));
                }
            });
        });
    }
}

