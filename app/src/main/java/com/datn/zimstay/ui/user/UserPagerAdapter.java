package com.datn.zimstay.ui.user;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class UserPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 2;

    public UserPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UserProfileFragment();
            case 1:
                return new UserSecurityFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
} 