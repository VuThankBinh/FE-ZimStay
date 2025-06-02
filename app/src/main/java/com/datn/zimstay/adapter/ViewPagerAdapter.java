package com.datn.zimstay.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.datn.zimstay.ApartmentListFragment;
import com.datn.zimstay.AppointmentFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ApartmentListFragment();
            case 1:
                return new AppointmentFragment();
            default:
                return new ApartmentListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
} 