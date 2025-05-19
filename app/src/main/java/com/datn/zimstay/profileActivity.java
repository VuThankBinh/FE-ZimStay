package com.datn.zimstay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.datn.zimstay.api.ApiService;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.TokenCheckResponse;
import com.datn.zimstay.api.models.UploadResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class profileActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private TextView tvUserName;
    private TextView tvEmail;
    private EditText tvPhoneNumber;
    private EditText tvAddress;
    private EditText tvAge;
    private EditText tvIdCard;
    private TextView tvViolations;

    private TextView pass;
    private Button btnEdit;

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        bottomNavigationView = findViewById(R.id.navView);
        // Thiết lập item được chọn là Practice
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(profileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_message) {
                    Intent intent = new Intent(profileActivity.this, messageActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_notification) {
                    Intent intent = new Intent(profileActivity.this, notificationActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {

                    return true;
                }
                return false;
            }
        });
        initializeViews();
       getUser();
    }
    
    private void initializeViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvAge = findViewById(R.id.tvAge);
        tvIdCard = findViewById(R.id.tvIdCard);
        tvViolations = findViewById(R.id.tvViolations);
        btnEdit = findViewById(R.id.btnEdit);
    }


   SharedPreferences sharedPreferences;
    private void getUser() {
        sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            RetrofitClient.getInstance()
                    .getApi()
                    .getUser("Bearer " + token)
                    .enqueue(new Callback<TokenCheckResponse>() {
                        @Override
                        public void onResponse(Call<TokenCheckResponse> call, Response<TokenCheckResponse> response) {
                            System.out.println("res is success" + response.isSuccessful());
                            System.out.println("res is body" + response.body());
                            if (response.isSuccessful() && response.body() != null) {
                                TokenCheckResponse tokenCheckResponse = response.body();
                                TokenCheckResponse.TokenData tokenData = tokenCheckResponse.getData();
                                // Gọi update UI hoặc truyền sang ViewModel
                                String jsonString = new Gson().toJson(tokenData);
                                System.out.println(jsonString);
                                updateUser(tokenData);
                            } else {
                                // Xử lý lỗi
                                Toast.makeText(profileActivity.this, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<TokenCheckResponse> call, Throwable t) {
                            // Xử lý lỗi
                            System.out.println("res is fail: " + t.getMessage());
                        }
                    });
        }
    }

    private void updateUser(TokenCheckResponse.TokenData tokenData) {
        if (tokenData == null) return;

        tvUserName.setText(tokenData.getUserName());
        tvEmail.setText(tokenData.getEmail());
        tvPhoneNumber.setText(tokenData.getPhoneNumber());
        tvAddress.setText(tokenData.getAddress());
        tvAge.setText(String.valueOf(tokenData.getAge()));
        tvIdCard.setText(tokenData.getIdCardNumber());
        tvViolations.setText(String.valueOf(tokenData.getNumOfViolations()));
        Glide.with(this)
                .load(RetrofitClient.getInstance()
                        .getApi().getImage() +"/"+tokenData.getAvatar()) // URL ảnh
                .placeholder(R.drawable.user_1) // ảnh mặc định nếu chưa load
                .error(R.drawable.user_2) // ảnh khi load lỗi
                .circleCrop() // nếu muốn ảnh tròn
                .into(ivAvatar);
    }
}