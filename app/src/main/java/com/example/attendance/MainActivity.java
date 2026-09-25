package com.example.attendance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.attendance.databinding.ActivityMainBinding;
import com.example.attendance.ui.attendance.FaceVerificationActivity;
import com.example.attendance.ui.auth.LoginActivity;
import com.example.attendance.ui.main.DirectoryFragment;
import com.example.attendance.ui.main.HomeFragment;
import com.example.attendance.ui.main.ProfileTabFragment;
import com.example.attendance.ui.notification.NotificationActivity;
import com.example.attendance.utils.SessionManager;
import com.example.attendance.worker.HeartbeatWorker;
import com.example.attendance.worker.SyncWorker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        requestRequiredPermissions();
        setupNavigationTabs();
        scheduleBackgroundWorkers();

        // Load default Home tab
        switchTab(new HomeFragment(), "Home");
    }

    private void setupNavigationTabs() {
        binding.navHome.setOnClickListener(v -> {
            switchTab(new HomeFragment(), "Home");
            updateTabIcons(1);
        });

        binding.navDirectory.setOnClickListener(v -> {
            switchTab(new DirectoryFragment(), "Danh bạ");
            updateTabIcons(2);
        });

        binding.btnFingerprint.setOnClickListener(v -> {
            startActivity(new Intent(this, FaceVerificationActivity.class));
        });

        binding.navNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        binding.navProfile.setOnClickListener(v -> {
            switchTab(new ProfileTabFragment(), "Hồ sơ cá nhân");
            updateTabIcons(5);
        });
    }

    private void switchTab(Fragment fragment, String title) {
        binding.tvAppTitle.setText(title);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void updateTabIcons(int selectedTab) {
        binding.ivNavHome.setColorFilter(ContextCompat.getColor(this, selectedTab == 1 ? R.color.primary : R.color.text_secondary));
        binding.ivNavDirectory.setColorFilter(ContextCompat.getColor(this, selectedTab == 2 ? R.color.primary : R.color.text_secondary));
        binding.ivNavNotification.setColorFilter(ContextCompat.getColor(this, selectedTab == 4 ? R.color.primary : R.color.text_secondary));
    }

    private void requestRequiredPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
        };

        boolean needsRequest = false;
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                needsRequest = true;
                break;
            }
        }

        if (needsRequest) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void scheduleBackgroundWorkers() {
        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(SyncWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("SyncWorkerJob", ExistingPeriodicWorkPolicy.KEEP, syncWorkRequest);

        PeriodicWorkRequest heartbeatWorkRequest = new PeriodicWorkRequest.Builder(HeartbeatWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("HeartbeatWorkerJob", ExistingPeriodicWorkPolicy.KEEP, heartbeatWorkRequest);
    }
}
