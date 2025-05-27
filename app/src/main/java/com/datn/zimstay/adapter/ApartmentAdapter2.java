package com.datn.zimstay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.zimstay.R;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.api.models.ApartmentData;
import com.datn.zimstay.api.models.ImageItem;
import com.datn.zimstay.model.Apartment;
import com.datn.zimstay.model.Image;

import java.util.List;

public class ApartmentAdapter2 extends RecyclerView.Adapter<ApartmentAdapter2.ApartmentViewHolder> {
    private Context context;
    private List<ApartmentData> apartments;
    private OnApartmentClickListener2 listener;

    public ApartmentAdapter2(Context context, List<ApartmentData> apartments, OnApartmentClickListener2 listener) {
        this.context = context;
        this.apartments = apartments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ApartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_apartment_cost_asc, parent, false);
        return new ApartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApartmentViewHolder holder, int position) {
        ApartmentData apartment = apartments.get(position);
        holder.tvTitle.setText(apartment.getAddress());
        holder.tvAddress.setText(apartment.getWard() + ", " + apartment.getDistrict() + ", " + apartment.getCity());
        String formattedPrice = String.format("%,d", apartment.getCost());
        holder.tvPrice.setText(formattedPrice + " VNĐ/tháng");
//        holder.tvPrice.setText(apartment.getCost() + " VNĐ/tháng");
        // Nếu có ảnh thì load ảnh đầu tiên, nếu không thì để ảnh mặc định
        if (apartment.getImages() != null && !apartment.getImages().isEmpty()) {
            ImageItem img = apartment.getImages().get(0);
            String url = img.getImageUrl();
            if (!url.startsWith("http")) {
                url = ApiConfig.Base_url + "uploads/images/"+url;
                System.out.println("url("+position+"): "+url);
            }
            Glide.with(context)
                .load(url)
                .placeholder(R.drawable.img_trangchu1)
                .error(R.drawable.img_trangchu1)
                .into(holder.imgApartment);
        } else {
            holder.imgApartment.setImageResource(R.drawable.img_trangchu1);
        }
        // Thêm sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApartmentClick2(apartment.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return apartments.size();
    }

    public static class ApartmentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgApartment;
        TextView tvTitle, tvAddress, tvPrice;

        public ApartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgApartment = itemView.findViewById(R.id.imgApartment);
            tvTitle = itemView.findViewById(R.id.tvApartmentTitle);
            tvAddress = itemView.findViewById(R.id.tvApartmentAddress);
            tvPrice = itemView.findViewById(R.id.tvApartmentPrice);
        }
    }

    public interface OnApartmentClickListener2 {
        void onApartmentClick2(int apartmentId);
    }
}