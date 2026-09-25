package com.example.attendance.ui.request;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;
import com.example.attendance.data.model.response.RequestHistoryItem;

import java.util.ArrayList;
import java.util.List;

public class RequestHistoryAdapter extends RecyclerView.Adapter<RequestHistoryAdapter.ViewHolder> {
    private List<RequestHistoryItem> list = new ArrayList<>();

    public void setList(List<RequestHistoryItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestHistoryItem item = list.get(position);
        holder.tvRequestTitle.setText(item.getTitle());
        holder.tvRequestStatus.setText(item.getStatus());
        holder.tvRequestDate.setText(item.getDate());
        holder.tvRequestReason.setText(item.getReason());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RequestDetailActivity.class);
            intent.putExtra("REQUEST_ID", item.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRequestTitle, tvRequestStatus, tvRequestDate, tvRequestReason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRequestTitle = itemView.findViewById(R.id.tvRequestTitle);
            tvRequestStatus = itemView.findViewById(R.id.tvRequestStatus);
            tvRequestDate = itemView.findViewById(R.id.tvRequestDate);
            tvRequestReason = itemView.findViewById(R.id.tvRequestReason);
        }
    }
}
