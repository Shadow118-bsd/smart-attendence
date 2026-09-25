package com.example.attendance.ui.profile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.databinding.ActivityProfileDetailBinding;

public class ProfileDetailActivity extends AppCompatActivity {
    private ActivityProfileDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
    }
}
