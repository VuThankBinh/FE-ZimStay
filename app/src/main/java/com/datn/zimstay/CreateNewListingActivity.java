package com.datn.zimstay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.adapter.ApartmentSelectionAdapter;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.ApartmentsResponse;
import com.datn.zimstay.api.models.CreateListingResponse;
import com.datn.zimstay.model.Apartment;
import com.datn.zimstay.model.Listing;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewListingActivity extends AppCompatActivity {
    private EditText etTitle, etDescription;
    private RecyclerView rvApartments;
    private Button btnCreatePost;
    private ApartmentSelectionAdapter adapter;
    private List<Apartment> apartmentList;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_listing);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Lấy ID người dùng hiện tại từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("nguoi_dung_id", 0);

        // Khởi tạo views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        rvApartments = findViewById(R.id.rvApartments);
        btnCreatePost = findViewById(R.id.btnCreatePost);

        // Thiết lập RecyclerView
        apartmentList = new ArrayList<>();
        adapter = new ApartmentSelectionAdapter(apartmentList);
        rvApartments.setLayoutManager(new LinearLayoutManager(this));
        rvApartments.setAdapter(adapter);

        // Load danh sách apartments từ API
        loadApartments();

        // Xử lý sự kiện tạo bài đăng
        btnCreatePost.setOnClickListener(v -> createNewListing());
    }

    private void loadApartments() {
        RetrofitClient
                .getInstance()
                .getApi()
                .getApartmentsByOwner(currentUserId)
                .enqueue(new Callback<ApartmentsResponse>() {
                    @Override
                    public void onResponse(Call<ApartmentsResponse> call, Response<ApartmentsResponse> response) {
                        ApartmentsResponse apartmentsResponse = response.body();
                        if (response.isSuccessful() && apartmentsResponse != null) {
                            apartmentList.clear();
                            apartmentList.addAll(apartmentsResponse.getData());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CreateNewListingActivity.this,
                                    apartmentsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApartmentsResponse> call, Throwable t) {

                    }
                });
    }

    private void createNewListing() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        List<Integer> selectedApartmentIds = adapter.getSelectedApartmentIds();

        if (title.isEmpty()) {
            etTitle.setError("Vui lòng nhập tiêu đề");
            return;
        }

        if (description.isEmpty()) {
            etDescription.setError("Vui lòng nhập mô tả");
            return;
        }

        if (selectedApartmentIds.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một phòng trọ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Listing mới
        Listing newListing = new Listing();
        newListing.setTitle(title);
        newListing.setDescription(description);
        newListing.setApartmentIds(selectedApartmentIds);
        newListing.setUserId(currentUserId);
        newListing.setStatus(false);

        Gson gson = new Gson();
        System.out.println("request listing: " + gson.toJson(newListing));
        // Gọi API tạo bài đăng mới
        RetrofitClient
                .getInstance()
                .getApi()
                .createListing(newListing)
                .enqueue(new Callback<CreateListingResponse>() {
                    @Override
                    public void onResponse(Call<CreateListingResponse> call, Response<CreateListingResponse> response) {
                        CreateListingResponse createListingResponse = response.body();
                        if (response.isSuccessful() && createListingResponse != null) {
                            Toast.makeText(CreateNewListingActivity.this,
                                    createListingResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                JsonObject jsonObject = JsonParser.parseString(errorBody).getAsJsonObject();
                                String errorMessage = jsonObject.get("message").getAsString();
                                Toast.makeText(CreateNewListingActivity.this,
                                        errorMessage, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                Toast.makeText(CreateNewListingActivity.this,
                                        "Không thể tạo bài đăng", Toast.LENGTH_SHORT).show();
                            }
                            System.out.println("reso: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateListingResponse> call, Throwable t) {
                        Toast.makeText(CreateNewListingActivity.this,
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}