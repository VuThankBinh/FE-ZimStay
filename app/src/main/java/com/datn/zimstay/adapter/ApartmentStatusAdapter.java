package com.datn.zimstay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.zimstay.R;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.model.Apartment;
import com.datn.zimstay.model.Image;

import java.util.List;

public class ApartmentStatusAdapter extends RecyclerView.Adapter<ApartmentStatusAdapter.ApartmentViewHolder> {
    private Context context;
    private List<Apartment> apartments;
    private OnApartmentClickListener listener;
    private String statusFilter = "ALL";

    public interface OnApartmentClickListener {
        void onApartmentClick(int apartmentId);
        void onEditStatusClick(int apartmentId, String currentStatus);
        void onEditInfoClick(int apartmentId);
    }

    public ApartmentStatusAdapter(Context context, List<Apartment> apartments, OnApartmentClickListener listener) {
        this.context = context;
        this.apartments = apartments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ApartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_apartment_status, parent, false);
        return new ApartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApartmentViewHolder holder, int position) {
        Apartment apartment = apartments.get(position);
        holder.tvTitle.setText(apartment.getAddress());
        holder.tvAddress.setText(apartment.getWard() + ", " + apartment.getDistrict() + ", " + apartment.getCity());
        String formattedPrice = String.format("%,d", apartment.getCost());
        holder.tvPrice.setText(formattedPrice + " VNĐ/tháng");

        // Hiển thị trạng thái phòng trọ
        String status = apartment.getStatus();
        if (status != null) {
            if (status.equalsIgnoreCase("AVAILABLE")) {
                holder.tvStatus.setText("Còn trống");
                holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
            } else if (status.equalsIgnoreCase("UNAVAILABLE")) {
                holder.tvStatus.setText("Đã cho thuê");
                holder.tvStatus.setTextColor(context.getResources().getColor(R.color.red));
            } else {
                holder.tvStatus.setText("Chưa xác định");
                holder.tvStatus.setTextColor(context.getResources().getColor(R.color.gray));
            }
        } else {
            holder.tvStatus.setText("Chưa xác định");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.gray));
        }

        // Load ảnh
        if (apartment.getImages() != null && !apartment.getImages().isEmpty()) {
            Image img = apartment.getImages().get(0);
            String url = img.getImageUrl();
            if (!url.startsWith("http")) {
                url = ApiConfig.Base_url + "uploads/images/"+url;
            }
            Glide.with(context)
                .load(url)
                .placeholder(R.drawable.img_trangchu1)
                .error(R.drawable.img_trangchu1)
                .into(holder.imgApartment);
        } else {
            holder.imgApartment.setImageResource(R.drawable.img_trangchu1);
        }

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApartmentClick(apartment.getId());
            }
        });

        // Xử lý sự kiện click nút sửa trạng thái
        holder.btnEditStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditStatusClick(apartment.getId(), apartment.getStatus());
            }
        });

        // Xử lý sự kiện click nút sửa thông tin
        holder.btnEditInfo.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditInfoClick(apartment.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return apartments.size();
    }

    public void setStatusFilter(String status) {
        this.statusFilter = status;
        notifyDataSetChanged();
    }

    public static class ApartmentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgApartment;
        TextView tvTitle, tvAddress, tvPrice, tvStatus;
        Button btnEditStatus, btnEditInfo;

        public ApartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgApartment = itemView.findViewById(R.id.imgApartment);
            tvTitle = itemView.findViewById(R.id.tvApartmentTitle);
            tvAddress = itemView.findViewById(R.id.tvApartmentAddress);
            tvPrice = itemView.findViewById(R.id.tvApartmentPrice);
            tvStatus = itemView.findViewById(R.id.tvApartmentStatus);
            btnEditStatus = itemView.findViewById(R.id.btnEditStatus);
            btnEditInfo = itemView.findViewById(R.id.btnEditInfo);
        }
    }
} 