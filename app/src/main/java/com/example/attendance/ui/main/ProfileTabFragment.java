package com.example.attendance.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.attendance.databinding.FragmentProfileTabBinding;
import com.example.attendance.ui.auth.LoginActivity;
import com.example.attendance.ui.profile.ProfileDetailActivity;
import com.example.attendance.utils.SessionManager;

public class ProfileTabFragment extends Fragment {
    private FragmentProfileTabBinding binding;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());

        // 1. Hồ sơ cá nhân
        binding.btnProfileDetail.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ProfileDetailActivity.class));
        });

        // 2. Cài đặt
        binding.btnSettings.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Màn hình cài đặt", Toast.LENGTH_SHORT).show();
        });

        // 3. Đổi mật khẩu
        binding.btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Màn hình đổi mật khẩu", Toast.LENGTH_SHORT).show();
        });

        // 4. Đăng xuất
        binding.btnLogoutTab.setOnClickListener(v -> {
            sessionManager.clear();
            Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
