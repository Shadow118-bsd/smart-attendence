package com.example.attendance.ui.approval;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.attendance.databinding.ActivityApprovalListBinding;
import com.example.attendance.viewmodel.ApprovalViewModel;

public class ApprovalListActivity extends AppCompatActivity {
    private ActivityApprovalListBinding binding;
    private ApprovalViewModel viewModel;
    private ApprovalListAdapter adapter;
    private String approvalType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApprovalListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ApprovalViewModel.class);

        approvalType = getIntent().getStringExtra("TITLE");
        if (approvalType != null && !approvalType.isEmpty()) {
            binding.tvApprovalTitle.setText(approvalType);
        } else {
            approvalType = "Phê duyệt";
        }

        binding.btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
        setupObservers();
    }

    private void setupRecyclerView() {
        adapter = new ApprovalListAdapter();
        binding.rvApprovalList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvApprovalList.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getPendingApprovalList().observe(this, list -> {
            if (list != null && !list.isEmpty()) {
                binding.emptyView.setVisibility(View.GONE);
                binding.rvApprovalList.setVisibility(View.VISIBLE);
                adapter.setList(list);
            } else {
                binding.emptyView.setVisibility(View.VISIBLE);
                binding.rvApprovalList.setVisibility(View.GONE);
                adapter.setList(null);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadPendingApprovals(approvalType);
    }
}
