package com.example.attendance.ui.approval;

import android.content.Context;
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

public class ApprovalListAdapter extends RecyclerView.Adapter<ApprovalListAdapter.ViewHolder> {
    private List<ApprovalHistoryItem> list = new ArrayList<>();

    public void setList(List<ApprovalHistoryItem> list) {
        this.list = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_approval_pending, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApprovalHistoryItem item = list.get(position);
        holder.tvEmployeeName.setText(item.getEmployeeName());
        holder.tvRequestType.setText(item.getRequestType());
        holder.tvReason.setText(item.getReason() != null ? item.getReason() : "");
        holder.tvRequestDate.setText("Ngày gửi: " + (item.getRequestDate() != null ? item.getRequestDate() : ""));
        holder.tvStatus.setText(item.getStatus() != null ? item.getStatus() : "Chờ duyệt");

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ApprovalDetailActivity.class);
            intent.putExtra("APPROVAL_ID", item.getId());
            intent.putExtra("IS_PENDING", true);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeName, tvRequestType, tvReason, tvRequestDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvRequestType = itemView.findViewById(R.id.tvRequestType);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvRequestDate = itemView.findViewById(R.id.tvRequestDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
