package com.example.attendance.ui.businesstrip;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.databinding.ActivityBusinessTripBinding;

public class BusinessTripActivity extends AppCompatActivity {
    private ActivityBusinessTripBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusinessTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Đăng ký công tác");
        }

        binding.btnSubmitTrip.setOnClickListener(v -> {
            String location = binding.etLocation.getText() != null ? binding.etLocation.getText().toString() : "";
            if (location.trim().isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa điểm công tác!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Đã gửi đơn công tác thành công (Trạng thái: PENDING)", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
