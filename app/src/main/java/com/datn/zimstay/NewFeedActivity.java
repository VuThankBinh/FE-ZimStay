package com.datn.zimstay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.UpgradeVipActivity;
import com.datn.zimstay.adapter.PostAdapter;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.ListingCountResponse;
import com.datn.zimstay.fragment.PostListFragment;
import com.datn.zimstay.model.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class NewFeedActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private int currentUserId;
    private TextView txtPostLimitStatus;
    private TextView txtUpgradeSuggestion;
    private static final int MAX_POSTS_PER_MONTH = 5;
    private List<Post> allPosts = new ArrayList<>();
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_feed);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Khởi tạo views
        Button btnCreatePost = findViewById(R.id.btnCreatePost);
        txtPostLimitStatus = findViewById(R.id.txtPostLimitStatus);
        txtUpgradeSuggestion = findViewById(R.id.txtUpgradeSuggestion);
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        Button btnUpgradeAccount=findViewById(R.id.btnUpgradeAccount);
        btnUpgradeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewFeedActivity.this, UpgradeVipActivity.class);
                startActivity(intent);
            }
        });
        // Khởi tạo RecyclerView và Adapter
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(this, allPosts, post -> {
            // Xử lý sự kiện click vào bài đăng
            // TODO: Thêm xử lý khi click vào bài đăng
        });
        recyclerViewPosts.setAdapter(postAdapter);

        // Lấy ID người dùng hiện tại từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("nguoi_dung_id", 0);

        // Xử lý sự kiện tạo bài đăng
        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewFeedActivity.this, CreateNewListingActivity.class);
                startActivity(intent);
            }
        });

        // Lấy số lượng bài đăng
        loadListingCount();

        // Thiết lập Bottom Navigation
        setupBottomNavigation();

        TabLayout tabLayout = findViewById(R.id.tabLayoutPosts);
        tabLayout.addTab(tabLayout.newTab().setText("Đã duyệt"));
        tabLayout.addTab(tabLayout.newTab().setText("Chưa duyệt"));

        // Hiển thị Fragment mặc định
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, PostListFragment.newInstance(currentUserId, true))
            .commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                boolean isApproved = tab.getPosition() == 0;
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, PostListFragment.newInstance(currentUserId, isApproved))
                    .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Không cần xử lý gì khi tab không được chọn
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Không cần xử lý gì khi tab được chọn lại
            }
        });
    }

    private void loadListingCount() {
        RetrofitClient
                .getInstance()
                .getApi()
                .getListingCountByUser(currentUserId)
                .enqueue(new Callback<ListingCountResponse>() {
                    @Override
                    public void onResponse(Call<ListingCountResponse> call, Response<ListingCountResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ListingCountResponse countResponse = response.body();
                            int currentCount = countResponse.getData().getCurrentCount();
                            int maxAllowed = countResponse.getData().getMaxAllowed();
                            updatePostLimitUI(currentCount, maxAllowed);
                        }
                    }

                    @Override
                    public void onFailure(Call<ListingCountResponse> call, Throwable t) {
                        // Xử lý lỗi nếu cần
                    }
                });
    }

    private void updatePostLimitUI(int postCount, int maxAllowed) {
        String statusText = String.format("Đã đăng %d/%d bài trong tháng", postCount, maxAllowed);
        txtPostLimitStatus.setText(statusText);

        // Hiển thị gợi ý nâng cấp nếu đã đạt giới hạn
        if (postCount >= MAX_POSTS_PER_MONTH) {
            txtUpgradeSuggestion.setVisibility(View.VISIBLE);
        } else {
            txtUpgradeSuggestion.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {
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

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật số lượng bài đăng khi quay lại màn hình
        loadListingCount();
        // Reload danh sách bài đăng
        loadPostsByStatus(true); // Mặc định load bài đã duyệt
    }

    private void loadPostsByStatus(boolean isApproved) {
        RetrofitClient.getInstance().getApi()
            .getListingsByUserAndStatus(currentUserId, isApproved)
            .enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        allPosts = response.body();
                        Gson gson = new Gson();
                        System.out.println("allPosts: " + gson.toJson(allPosts));
                        postAdapter.setData(allPosts);
                    }
                }
                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    // Xử lý lỗi
                }
            });
    }
}