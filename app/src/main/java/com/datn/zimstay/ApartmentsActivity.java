package com.datn.zimstay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.datn.zimstay.adapter.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ApartmentsActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private String[] tabTitles = {"Danh sách phòng trọ", "Lịch hẹn"};
    private BottomNavigationView bottomNavigationView;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apartments);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        // Setup ViewPager2 và TabLayout
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Khởi tạo ViewPagerAdapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("nguoi_dung_id", 0);
        int accountType = sharedPreferences.getInt("account_type", 1);
        
        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.navView);
        bottomNavigationView.setSelectedItemId(R.id.nav_qlpt);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_bang_tin) {
                    Intent intent = new Intent(ApartmentsActivity.this, NewFeedActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_message) {
                    Intent intent = new Intent(ApartmentsActivity.this, messageActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_qlpt) {
                    return true;
                } else if (itemId == R.id.nav_notification) {
                    Intent intent = new Intent(ApartmentsActivity.this, notificationActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(ApartmentsActivity.this, profileActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
}