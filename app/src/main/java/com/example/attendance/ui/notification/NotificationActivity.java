package com.example.attendance.ui.notification;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.attendance.data.model.response.NotificationItem;
import com.example.attendance.data.repository.ProfileRepository;
import com.example.attendance.databinding.ActivityNotificationBinding;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding binding;
    private NotificationAdapter adapter;
    private ProfileRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new ProfileRepository();
        adapter = new NotificationAdapter();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnMarkAllRead.setOnClickListener(v -> {
            Toast.makeText(this, "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
        });

        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(adapter);

        repository.getNotifications(new ProfileRepository.ApiCallback<List<NotificationItem>>() {
            @Override
            public void onSuccess(List<NotificationItem> result) {
                adapter.setList(result);
            }

            @Override
            public void onError(String message) {}
        });
    }
}
