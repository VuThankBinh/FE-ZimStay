package com.datn.zimstay.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.zimstay.R;

import java.util.List;

public class ImageRecyclerViewAdapter  extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageViewHolder>{
    private List<String> imageUrls;
    private Context context;

    public ImageRecyclerViewAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item_recycler_view, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Glide.with(context)
                .load(imageUrls.get(position))
                .placeholder(R.drawable.ic_id_card)
                .into(holder.imgPhoto);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
    }
}
