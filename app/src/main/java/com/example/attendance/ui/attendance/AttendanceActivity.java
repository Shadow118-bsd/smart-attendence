package com.example.attendance.ui.attendance;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.attendance.data.model.request.FaceEvidence;
import com.example.attendance.data.model.request.LocationEvidence;
import com.example.attendance.data.model.request.WifiEvidence;
import com.example.attendance.databinding.ActivityAttendanceBinding;
import com.example.attendance.service.LocationService;
import com.example.attendance.service.WifiService;
import com.example.attendance.utils.AttendanceVerifier;
import com.example.attendance.utils.Resource;
import com.example.attendance.viewmodel.AttendanceViewModel;

public class AttendanceActivity extends AppCompatActivity {
    private ActivityAttendanceBinding binding;
    private AttendanceViewModel viewModel;
    private WifiService wifiService;
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);
        wifiService = new WifiService(this);
        locationService = new LocationService(this);

        setupObservers();

        if (!isGpsEnabled()) {
            Toast.makeText(this, "Vui lòng bật định vị GPS phần cứng để điểm danh!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        String action = getIntent().getStringExtra("ACTION");
        boolean verified = getIntent().getBooleanExtra("FACE_VERIFIED", false);
        boolean liveness = getIntent().getBooleanExtra("LIVENESS_PASSED", false);
        String faceImagePath = getIntent().getStringExtra("FACE_IMAGE_PATH");
        FaceEvidence faceEvidence = new FaceEvidence(verified, liveness);

        locationService.getCurrentLocation(locationEvidence -> {
            WifiEvidence wifiEvidence = wifiService.collectWifiEvidence();
            AttendanceVerifier.VerificationResult verResult = AttendanceVerifier.verify(faceEvidence, locationEvidence, wifiEvidence);

            runOnUiThread(() -> {
                if ("CHECK_IN".equals(action)) {
                    binding.tvActionTitle.setText("KẾT QUẢ CHECK-IN");
                    viewModel.checkIn(faceEvidence, faceImagePath);
                } else {
                    binding.tvActionTitle.setText("KẾT QUẢ CHECK-OUT");
                    viewModel.checkOut();
                }
                binding.tvEvidenceDetails.setText(verResult.getDetailsMessage());
            });
        });

        binding.btnDone.setOnClickListener(v -> finish());
    }

    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBarAttendance.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getCheckInState().observe(this, resource -> {
            if (resource == null) return;
            handleResourceState(resource);
        });

        viewModel.getCheckOutState().observe(this, resource -> {
            if (resource == null) return;
            handleResourceState(resource);
        });
    }

    private <T> void handleResourceState(Resource<T> resource) {
        switch (resource.status) {
            case LOADING:
                binding.progressBarAttendance.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
            case OFFLINE_SAVED:
            case ERROR:
                binding.progressBarAttendance.setVisibility(View.GONE);
                binding.btnDone.setVisibility(View.VISIBLE);
                showTicketBottomSheet(getIntent().getStringExtra("ACTION"));
                break;
        }
    }

    private void showTicketBottomSheet(String action) {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(com.example.attendance.R.layout.dialog_attendance_ticket, null);
        dialog.setContentView(dialogView);

        android.widget.TextView tvTitle = dialogView.findViewById(com.example.attendance.R.id.tvTicketTitle);
        android.widget.TextView tvTime = dialogView.findViewById(com.example.attendance.R.id.tvTicketTime);
        android.widget.TextView tvWifi = dialogView.findViewById(com.example.attendance.R.id.tvTicketWifi);
        android.widget.Button btnDone = dialogView.findViewById(com.example.attendance.R.id.btnDoneTicket);

        String titleStr = "CHECK_IN".equals(action) ? "VÀO CA (CHECK-IN) THÀNH CÔNG" : "RA CA (CHECK-OUT) THÀNH CÔNG";
        tvTitle.setText(titleStr);

        String currentTimeStr = new java.text.SimpleDateFormat("HH:mm:ss - dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
        tvTime.setText(currentTimeStr);

        if (wifiService != null) {
            WifiEvidence wifi = wifiService.collectWifiEvidence();
            tvWifi.setText(wifi != null && wifi.getSsid() != null ? wifi.getSsid() : "FPT_Office_5G");
        }

        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }
}

