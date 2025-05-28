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

import com.datn.zimstay.api.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.datn.zimstay.adapter.ConversationAdapter;
import com.datn.zimstay.api.models.ConversationResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class messageActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView, bottomNavigationView2;
    private RecyclerView recyclerConversations;
    private ConversationAdapter conversationAdapter;
    private List<ConversationResponse> conversationList = new ArrayList<>();
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Lấy ID người dùng hiện tại từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("nguoi_dung_id", 0);
        int accountType = sharedPreferences.getInt("account_type", 1);

        bottomNavigationView = findViewById(R.id.navView);
        bottomNavigationView.setSelectedItemId(R.id.nav_message);
        bottomNavigationView2 = findViewById(R.id.navView2);
        bottomNavigationView2.setSelectedItemId(R.id.nav_message);
        if(accountType==1){
            bottomNavigationView2.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        else{
            bottomNavigationView2.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.GONE);
        }

        recyclerConversations = findViewById(R.id.recycler_conversations);
        conversationAdapter = new ConversationAdapter(this, conversationList, currentUserId);
        recyclerConversations.setLayoutManager(new LinearLayoutManager(this));
        recyclerConversations.setAdapter(conversationAdapter);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(messageActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_message) {
                    return true;
                } else if (itemId == R.id.nav_search) {
                    Intent intent = new Intent(messageActivity.this, searchActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                } else if (itemId == R.id.nav_notification) {
                    Intent intent = new Intent(messageActivity.this, notificationActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(messageActivity.this, profileActivity.class);
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
                    Intent intent = new Intent(messageActivity.this, NewFeedActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_message) {
                    return true;
                } else if (itemId == R.id.nav_qlpt) {
                    Intent intent = new Intent(messageActivity.this, ApartmentsActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                } else if (itemId == R.id.nav_notification) {
                    Intent intent = new Intent(messageActivity.this, notificationActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(messageActivity.this, profileActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        System.out.println("currentUserId:"+currentUserId);
        // Sau khi gọi API:
        RetrofitClient.getInstance().getApi().getConversationsByUserId(currentUserId)
            .enqueue(new Callback<ArrayList<ConversationResponse>>() {
                @Override
                public void onResponse(Call<ArrayList<ConversationResponse>> call, Response<ArrayList<ConversationResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        System.out.println(json);
                        conversationList.clear();
                        conversationList.addAll(response.body());
                        conversationAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onFailure(Call<ArrayList<ConversationResponse>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
    }
}