package com.datn.zimstay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.datn.zimstay.api.ApiService;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.ResetPasswordRequest;
import com.datn.zimstay.api.models.ResetPasswordResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class resetPassActivity extends AppCompatActivity {
    private TextInputLayout tilPassword, tilConfirmPassword;
    private TextInputEditText etPassword, etConfirmPassword;
    private Button btnConfirm;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_pass);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Ánh xạ view
        initViews();
        
        // Xử lý sự kiện
        setupListeners();
        Intent intent = getIntent();
        email=intent.getStringExtra("email");
    }

    private void initViews() {
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnConfirm = findViewById(R.id.btnRegister);
    }

    private void setupListeners() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    resetPassword();
                }
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Vui lòng nhập mật khẩu mới");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu không khớp");
            isValid = false;
        } else {
            tilConfirmPassword.setError(null);
        }

        return isValid;
    }

    private void resetPassword() {
        String newPassword = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
            return;
        }

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setNewPassword(newPassword);
        request.setConfirmNewPassword(confirmPassword);
        request.setCurrentPassword("string");
        request.setEmail(email);

        RetrofitClient.getInstance()
                .getApi()
                .resetPassword(request)
                .enqueue(new Callback<ResetPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                        Gson gson = new Gson();
                        String json = gson.toJson(request);
                        Log.d("RegisterRequestJSON", json);
                        System.out.println("JSON gửi đi: " + gson.toJson(request));
                        Gson gson2 = new Gson();
                        String json2 = gson2.toJson(response.body());
                        Log.d("RegisterRequestJSON", json);
                        System.out.println("JSON gửi đi: " + gson.toJson(response.body()));
                        if (response.isSuccessful() && response.body() != null) {
                            ResetPasswordResponse resetResponse = response.body();
                            if ("success".equalsIgnoreCase(resetResponse.getStatus())) {
                                Toast.makeText(resetPassActivity.this, "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(resetPassActivity.this, resetResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(resetPassActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                        System.out.println("lỗi: "+t.getMessage());
                        Toast.makeText(resetPassActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}