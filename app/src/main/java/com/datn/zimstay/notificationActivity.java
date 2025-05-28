package com.datn.zimstay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.adapter.NotificationAdapter;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.NotificationResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class notificationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView, bottomNavigationView2;
    private RecyclerView recyclerNotifications;
    private NotificationAdapter NotificationAdapter;
    private List<NotificationResponse> NotificationList = new ArrayList<>();
    private int currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Lấy ID người dùng hiện tại từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("nguoi_dung_id", 0);
        int accountType = sharedPreferences.getInt("account_type", 1);

        bottomNavigationView = findViewById(R.id.navView);
        bottomNavigationView.setSelectedItemId(R.id.nav_notification);
        bottomNavigationView2 = findViewById(R.id.navView2);
        bottomNavigationView2.setSelectedItemId(R.id.nav_notification);
        if(accountType==1){
            bottomNavigationView2.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        else{
            bottomNavigationView2.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.GONE);
        }

        recyclerNotifications = findViewById(R.id.recycler_notifications);
        NotificationAdapter = new NotificationAdapter(this, NotificationList, currentUserId);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotifications.setAdapter(NotificationAdapter);

        // Set listener để reload danh sách khi thông báo được đánh dấu đã đọc
        NotificationAdapter.setOnNotificationClickListener(() -> fetchNotifications());

        // Lấy danh sách thông báo
        fetchNotifications();

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(notificationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_message) {
                    Intent intent = new Intent(notificationActivity.this, messageActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_search) {
                    Intent intent = new Intent(notificationActivity.this, searchActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                } else if (itemId == R.id.nav_notification) {

                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(notificationActivity.this, profileActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
        bottomNavigationView2.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_bang_tin) {
                    Intent intent = new Intent(notificationActivity.this, NewFeedActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_message) {
                    Intent intent = new Intent(notificationActivity.this, messageActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_qlpt) {
                    Intent intent = new Intent(notificationActivity.this, ApartmentsActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                } else if (itemId == R.id.nav_notification) {

                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(notificationActivity.this, profileActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    private void fetchNotifications() {
        RetrofitClient
                .getInstance()
                .getApi()
                .getNotificationsByUserId(currentUserId)
                .enqueue(new Callback<List<NotificationResponse>>() {
                    @Override
                    public void onResponse(Call<List<NotificationResponse>> call, Response<List<NotificationResponse>> response) {
                        List<NotificationResponse> notifications = response.body();
                        if (notifications != null) {
                            Gson gson = new Gson();
                            String json = gson.toJson(notifications);
                            System.out.println("thong bao: "+json);
                        }
                        if (response.isSuccessful() && response.body() != null) {
                            NotificationList.clear();
                            NotificationList.addAll(notifications);
                            NotificationAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<NotificationResponse>> call, Throwable t) {

                    }
                });
    }
}