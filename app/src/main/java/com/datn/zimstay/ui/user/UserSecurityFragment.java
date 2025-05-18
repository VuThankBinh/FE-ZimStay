package com.datn.zimstay.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.datn.zimstay.R;
import com.datn.zimstay.resetPassActivity;
import com.google.android.material.card.MaterialCardView;

public class UserSecurityFragment extends Fragment {
    private MaterialCardView cardChangePassword, cardTwoFactor, cardLoginHistory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_security, container, false);
        
        // Ánh xạ view
        initViews(view);
        
        // Xử lý sự kiện
        setupListeners();
        
        return view;
    }

    private void initViews(View view) {
        cardChangePassword = view.findViewById(R.id.cardChangePassword);
        cardTwoFactor = view.findViewById(R.id.cardTwoFactor);
        cardLoginHistory = view.findViewById(R.id.cardLoginHistory);
    }

    private void setupListeners() {
        // Đổi mật khẩu
        cardChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), resetPassActivity.class);
            startActivity(intent);
        });

        // Xác thực 2 yếu tố
        cardTwoFactor.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        // Lịch sử đăng nhập
        cardLoginHistory.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }
} 