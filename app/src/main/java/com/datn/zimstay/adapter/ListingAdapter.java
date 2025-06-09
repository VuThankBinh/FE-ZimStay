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
import com.datn.zimstay.model.Listing;
import java.util.ArrayList;
import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {
    private List<Listing> listings = new ArrayList<>();
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Listing listing);
    }

    public ListingAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(List<Listing> data) {
        this.listings = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listing, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        Listing listing = listings.get(position);
        holder.tvTitle.setText(listing.getTitle());
        holder.tvDescription.setText(listing.getDescription());
        // Ảnh tĩnh: lấy từ trường images của apartment đầu tiên trong rooms (nếu có)
        String imageUrl = null;
//        if (listing.getRooms() != null && !listing.getRooms().isEmpty()) {
//            // Giả sử bạn có trường images trong room hoặc lấy từ API khác, ở đây chỉ demo lấy ảnh đầu tiên
//            List<String> images = listing.getRooms().get(0).getImages(); // Cần đảm bảo model Room có getImages()
//            if (images != null && !images.isEmpty()) {
//                imageUrl = images.get(0);
//            }
//        }
        if (imageUrl != null) {
            Glide.with(context).load(imageUrl).placeholder(R.drawable.img_trangchu1).into(holder.ivApartmentImage);
        } else {
            holder.ivApartmentImage.setImageResource(R.drawable.img_trangchu1);
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(listing));
    }

    @Override
    public int getItemCount() {
        return listings != null ? listings.size() : 0;
    }

    public static class ListingViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        ImageView ivApartmentImage;
        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivApartmentImage = itemView.findViewById(R.id.ivApartmentImage);
        }
    }
}
