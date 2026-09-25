package com.example.attendance.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.attendance.data.model.response.LoginResponse;
import com.example.attendance.data.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repository;
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.repository = new AuthRepository(application);
    }

    public LiveData<LoginResponse> getLoginResult() { return loginResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập tên đăng nhập và mật khẩu");
            return;
        }

        isLoading.setValue(true);
        repository.login(username, password, new AuthRepository.AuthCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                isLoading.setValue(false);
                loginResult.setValue(result);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public boolean isLoggedIn() {
        return repository.isLoggedIn();
    }

    public void logout() {
        repository.logout();
    }
}
