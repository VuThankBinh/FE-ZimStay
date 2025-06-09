package com.datn.zimstay.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.R;
import com.datn.zimstay.adapter.PostAdapter;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.model.Post;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListFragment extends Fragment {
    private static final String TAG = "PostListFragment";
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private int userId;
    private boolean isApproved;

    public static PostListFragment newInstance(int userId, boolean isApproved) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        args.putBoolean("isApproved", isApproved);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("userId");
            isApproved = getArguments().getBoolean("isApproved");
        }
        Log.d(TAG, "onCreate: userId=" + userId + ", isApproved=" + isApproved);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        
        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new PostAdapter(getContext(), new ArrayList<>(), post -> {
            // Xử lý sự kiện click vào bài đăng
            Toast.makeText(getContext(), "Click vào bài đăng: " + post.getTitle(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);
        
        loadPosts();
        
        return view;
    }

    private void loadPosts() {
        Log.d(TAG, "loadPosts: Bắt đầu gọi API với userId=" + userId + ", isApproved=" + isApproved);
        
        // Log URL đầy đủ
        String url = RetrofitClient.getInstance().getApi()
            .getListingsByUserAndStatus(userId, isApproved)
            .request()
            .url()
            .toString();
        Log.d(TAG, "API URL: " + url);
        
        RetrofitClient.getInstance().getApi()
            .getListingsByUserAndStatus(userId, isApproved)
            .enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    Log.d(TAG, "onResponse: Response code=" + response.code());
                    
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            List<Post> posts = response.body();
                            Gson gson = new Gson();
                            String jsonResponse = gson.toJson(posts);
                            Log.d(TAG, "onResponse: Data=" + jsonResponse);
                            
                            if (posts.isEmpty()) {
                                Log.d(TAG, "onResponse: Danh sách bài đăng trống");
                                Toast.makeText(getContext(), "Không có bài đăng nào", Toast.LENGTH_SHORT).show();
                            } else {
                                adapter.setData(posts);
                            }
                        } else {
                            Log.e(TAG, "onResponse: Response body is null");
                            Toast.makeText(getContext(), "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "onResponse: Error code=" + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Log.e(TAG, "onResponse: Error body=" + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "onResponse: Error reading error body", e);
                        }
                        Toast.makeText(getContext(), "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage(), t);
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
} 