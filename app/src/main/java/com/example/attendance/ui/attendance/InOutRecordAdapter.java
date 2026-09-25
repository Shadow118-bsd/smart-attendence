package com.example.attendance.ui.attendance;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;
import com.example.attendance.data.local.entity.AttendanceRecordEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InOutRecordAdapter extends RecyclerView.Adapter<InOutRecordAdapter.ViewHolder> {
    private List<AttendanceRecordEntity> list = new ArrayList<>();

    public void setList(List<AttendanceRecordEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_out_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceRecordEntity item = list.get(position);
        holder.tvTypeTitle.setText("CHECK_IN".equals(item.getEventType()) ? "CHECK-IN" : "CHECK-OUT");
        holder.tvTime.setText(item.getTimeString() + " - " + item.getDateString());

        holder.tvFaceBadge.setText(item.isFaceVerified() ? "FaceID: Đạt" : "FaceID: Không");
        holder.tvGpsBadge.setText(item.isGpsVerified() ? "GPS: Hợp lệ" : "GPS: Ngoài phạm vi");
        holder.tvWifiBadge.setText(item.isWifiVerified() ? "WiFi: " + item.getWifiSsid() : "WiFi: Không");

        holder.tvLocationDetails.setText("Lat/Lng: " + String.format("%.4f, %.4f", item.getLatitude(), item.getLongitude()) + " | CSDL Room DB");

        if (item.getFaceImagePath() != null && !item.getFaceImagePath().isEmpty()) {
            File photoFile = new File(item.getFaceImagePath());
            if (photoFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                holder.ivFacePhoto.setImageBitmap(bitmap);
                holder.ivFacePhoto.setVisibility(View.VISIBLE);

                holder.itemView.setOnClickListener(v -> showFacePhotoDialog(v.getContext(), item, bitmap));
                holder.ivFacePhoto.setOnClickListener(v -> showFacePhotoDialog(v.getContext(), item, bitmap));
            } else {
                holder.ivFacePhoto.setVisibility(View.GONE);
            }
        } else {
            holder.ivFacePhoto.setVisibility(View.GONE);
        }
    }

    private void showFacePhotoDialog(android.content.Context context, AttendanceRecordEntity item, Bitmap bitmap) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_face_photo_preview);

        ImageView ivFullPhoto = dialog.findViewById(R.id.ivFullPhoto);
        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        TextView tvDialogDetails = dialog.findViewById(R.id.tvDialogDetails);
        View btnClose = dialog.findViewById(R.id.btnCloseDialog);

        tvDialogTitle.setText("BẢN GHI FACE ID - " + ("CHECK_IN".equals(item.getEventType()) ? "CHECK-IN" : "CHECK-OUT"));
        tvDialogDetails.setText("Thời gian: " + item.getTimeString() + " " + item.getDateString()
                + "\nWiFi: " + item.getWifiSsid()
                + "\nTọa độ: " + item.getLatitude() + ", " + item.getLongitude());

        if (bitmap != null) {
            ivFullPhoto.setImageBitmap(bitmap);
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTypeTitle, tvTime, tvFaceBadge, tvGpsBadge, tvWifiBadge, tvLocationDetails;
        ImageView ivFacePhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTypeTitle = itemView.findViewById(R.id.tvTypeTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvFaceBadge = itemView.findViewById(R.id.tvFaceBadge);
            tvGpsBadge = itemView.findViewById(R.id.tvGpsBadge);
            tvWifiBadge = itemView.findViewById(R.id.tvWifiBadge);
            tvLocationDetails = itemView.findViewById(R.id.tvLocationDetails);
            ivFacePhoto = itemView.findViewById(R.id.ivFacePhoto);
        }
    }
}
