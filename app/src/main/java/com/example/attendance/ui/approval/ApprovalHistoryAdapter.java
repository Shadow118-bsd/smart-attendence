package com.example.attendance.ui.approval;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;
import com.example.attendance.data.model.response.ApprovalHistoryItem;

import java.util.ArrayList;
import java.util.List;

public class ApprovalHistoryAdapter extends RecyclerView.Adapter<ApprovalHistoryAdapter.ViewHolder> {
    private List<ApprovalHistoryItem> list = new ArrayList<>();

    public void setList(List<ApprovalHistoryItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_approval_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApprovalHistoryItem item = list.get(position);
        holder.tvEmployeeName.setText(item.getEmployeeName());
        holder.tvRequestType.setText(item.getRequestType());
        holder.tvStatus.setText(item.getStatus());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ApprovalDetailActivity.class);
            intent.putExtra("APPROVAL_ID", item.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeName, tvRequestType, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvRequestType = itemView.findViewById(R.id.tvRequestType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
