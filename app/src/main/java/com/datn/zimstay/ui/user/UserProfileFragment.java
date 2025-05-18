package com.datn.zimstay.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.datn.zimstay.R;
import com.datn.zimstay.api.ApiService;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.UserProfile;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileFragment extends Fragment {
    private ShapeableImageView ivAvatar;
    private TextView tvUserName, tvEmail, tvPhoneNumber, tvAddress, tvAge, tvIdCard, tvViolations;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        
        // Ánh xạ view
        initViews(view);
        
        // Load thông tin người dùng
        loadUserProfile();
        
        return view;
    }

    private void initViews(View view) {
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvAge = view.findViewById(R.id.tvAge);
        tvIdCard = view.findViewById(R.id.tvIdCard);
        tvViolations = view.findViewById(R.id.tvViolations);
    }

    private void loadUserProfile() {
        RetrofitClient.getInstance()
                .getApi()
                .getUserProfile()
                .enqueue(new Callback<UserProfile>() {
                    @Override
                    public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserProfile profile = response.body();
                            updateUI(profile);
                        } else {
                            Toast.makeText(getContext(), "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfile> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(UserProfile profile) {
        // Load avatar
        if (profile.getAvatar() != null && !profile.getAvatar().isEmpty()) {
            Picasso.get()
                    .load(profile.getAvatar())
                    .placeholder(R.drawable.user_1)
                    .error(R.drawable.user_1)
                    .into(ivAvatar);
        }

        // Update text
        tvUserName.setText(profile.getUserName());
        tvEmail.setText(profile.getEmail());
        tvPhoneNumber.setText(profile.getPhoneNumber());
        tvAddress.setText(profile.getAddress());
        tvAge.setText(String.valueOf(profile.getAge()));
        tvIdCard.setText(profile.getIdCardNumber());
        tvViolations.setText(String.valueOf(profile.getNumOfViolations()));
    }
} 