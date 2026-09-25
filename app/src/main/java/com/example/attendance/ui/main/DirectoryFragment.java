package com.example.attendance.ui.main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.attendance.data.model.response.DirectoryUserItem;
import com.example.attendance.data.repository.ProfileRepository;
import com.example.attendance.databinding.FragmentDirectoryBinding;

import java.util.ArrayList;
import java.util.List;

public class DirectoryFragment extends Fragment {
    private FragmentDirectoryBinding binding;
    private DirectoryAdapter adapter;
    private ProfileRepository repository;
    private List<DirectoryUserItem> fullList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDirectoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new ProfileRepository();
        adapter = new DirectoryAdapter();

        binding.rvDirectory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDirectory.setAdapter(adapter);

        loadDirectoryData();

        binding.etSearchDirectory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadDirectoryData() {
        repository.getDirectoryList("", new ProfileRepository.ApiCallback<List<DirectoryUserItem>>() {
            @Override
            public void onSuccess(List<DirectoryUserItem> result) {
                fullList = result;
                adapter.setList(result);
            }

            @Override
            public void onError(String message) {}
        });
    }

    private void filter(String query) {
        if (query.isEmpty()) {
            adapter.setList(fullList);
            return;
        }
        List<DirectoryUserItem> filtered = new ArrayList<>();
        for (DirectoryUserItem item : fullList) {
            if (item.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                    item.getDepartment().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }
        adapter.setList(filtered);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
