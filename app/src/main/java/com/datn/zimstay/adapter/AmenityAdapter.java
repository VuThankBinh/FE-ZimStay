package com.datn.zimstay.adapter;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.R;
import com.datn.zimstay.api.models.Amenity;

import java.util.List;

public class AmenityAdapter extends RecyclerView.Adapter<AmenityAdapter.AmenityViewHolder> {
    private Context context;
    private List<Amenity> amenities;

    public AmenityAdapter(Context context, List<Amenity> amenities) {
        this.context = context;
        this.amenities = amenities;
    }

    @NonNull
    @Override
    public AmenityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_amenity, parent, false);
        return new AmenityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmenityViewHolder holder, int position) {
        Amenity amenity = amenities.get(position);
        holder.tvAmenityName.setText(amenity.getAmenityName() + " (" + amenity.getAmenityUnit() + ")");
        if (amenity.getAmenityId() >= 1 && amenity.getAmenityId() <= 5) {
            holder.etAmenityPrice.setVisibility(View.VISIBLE);
            holder.cbAmenity.setVisibility(View.GONE);
            holder.etAmenityPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            holder.etAmenityPrice.setText(String.valueOf(amenity.getPricePerUnit()));
        } else if (amenity.getAmenityId() >= 7 && amenity.getAmenityId() <= 12) {
            holder.etAmenityPrice.setVisibility(View.GONE);
            holder.cbAmenity.setVisibility(View.VISIBLE);
            holder.cbAmenity.setChecked(amenity.getPricePerUnit() > 0);
        }
    }

    @Override
    public int getItemCount() {
        return amenities.size();
    }

    public static class AmenityViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmenityName;
        EditText etAmenityPrice;
        CheckBox cbAmenity;

        public AmenityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmenityName = itemView.findViewById(R.id.tvAmenityName);
            etAmenityPrice = itemView.findViewById(R.id.etAmenityPrice);
            cbAmenity = itemView.findViewById(R.id.cbAmenity);
        }
    }
}