package com.datn.zimstay.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.R;
import com.datn.zimstay.model.Apartment;

import java.util.ArrayList;
import java.util.List;

public class ApartmentSelectionAdapter extends RecyclerView.Adapter<ApartmentSelectionAdapter.ApartmentViewHolder> {
    private List<Apartment> apartments;
    private List<Integer> selectedApartmentIds;

    public ApartmentSelectionAdapter(List<Apartment> apartments) {
        this.apartments = apartments;
        this.selectedApartmentIds = new ArrayList<>();
    }

    @NonNull
    @Override
    public ApartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartment_selection, parent, false);
        return new ApartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApartmentViewHolder holder, int position) {
        Apartment apartment = apartments.get(position);
        holder.tvApartmentAddress.setText(apartment.getAddress() +", "+ apartment.getDistrict() +", "+ apartment.getCity());
        
        holder.cbSelectApartment.setChecked(selectedApartmentIds.contains(apartment.getId()));
        
        holder.cbSelectApartment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedApartmentIds.contains(apartment.getId())) {
                    selectedApartmentIds.add(apartment.getId());
                }
            } else {
                selectedApartmentIds.remove(Integer.valueOf(apartment.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return apartments.size();
    }

    public List<Integer> getSelectedApartmentIds() {
        return selectedApartmentIds;
    }

    static class ApartmentViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelectApartment;
        TextView tvApartmentName;
        TextView tvApartmentAddress;

        public ApartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelectApartment = itemView.findViewById(R.id.cbSelectApartment);
            tvApartmentName = itemView.findViewById(R.id.tvApartmentName);
            tvApartmentAddress = itemView.findViewById(R.id.tvApartmentAddress);
        }
    }
} 