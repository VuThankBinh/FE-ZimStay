package com.datn.zimstay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NewFeedActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView, bottomNavigationView2;

    private int currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_feed);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Lấy ID người dùng hiện tại từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("nguoi_dung_id", 0);
        int accountType = sharedPreferences.getInt("account_type", 1);

        bottomNavigationView = findViewById(R.id.navView);
        bottomNavigationView.setSelectedItemId(R.id.nav_bang_tin);




        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_bang_tin) {

                    return true;
                } else if (itemId == R.id.nav_message) {
                    Intent intent = new Intent(NewFeedActivity.this, messageActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_qlpt) {
                    Intent intent = new Intent(NewFeedActivity.this, ApartmentsActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                } else if (itemId == R.id.nav_notification) {
                    Intent intent = new Intent(NewFeedActivity.this, notificationActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(NewFeedActivity.this, profileActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

    }
}