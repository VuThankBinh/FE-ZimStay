package com.datn.zimstay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.api.ApiService;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.Amenity;
import com.datn.zimstay.api.models.ApartmentData;
import com.datn.zimstay.api.models.ApartmentResponse;
import com.datn.zimstay.api.models.ImageItem;
import com.datn.zimstay.adapter.ImageSliderAdapter;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class roomDetailActivity extends AppCompatActivity {

    Integer id=9;
    Integer ownerId=0;

    private ViewPager2 viewPager;
    private TextView tvStatus, tvAddress, tvCost, tvArea, tvDeposit, tvCity, tvDetailInfo;
    private LinearLayout layoutAmenities;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_detail);
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        initializeViews();
        Intent intent = getIntent();
        id = intent.getIntExtra("apartment_id", -1);
        fetchRoomDetail();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        tvStatus = findViewById(R.id.tvStatus);
        tvAddress = findViewById(R.id.tvAddress);
        tvCost = findViewById(R.id.tvCost);
        tvArea = findViewById(R.id.tvArea);
        tvDeposit = findViewById(R.id.tvDeposit);
        tvCity = findViewById(R.id.tvCity);
        tvDetailInfo = findViewById(R.id.tvDetailInfo);
        layoutAmenities = findViewById(R.id.layoutAmenities);
        back = findViewById(R.id.back);
    }
    private void fetchRoomDetail() {
        ApiService apiService = RetrofitClient.getInstance().getApi();
        Call<ApartmentResponse> call = apiService.getApartmentById(id);
        call.enqueue(new Callback<ApartmentResponse>() {
            @Override
            public void onResponse(Call<ApartmentResponse> call, Response<ApartmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApartmentResponse apartment = response.body();
                    ApartmentData data = apartment.getData();
                    Gson gson=new Gson();
                    System.out.println("data: "+gson.toJson(data));
                    ownerId= data.getOwnerId();
                    // Cập nhật UI với dữ liệu nhận được
                    tvStatus.setText(data.getStatus());
                    tvAddress.setText(data.getAddress());
                    tvCity.setText(data.getCity());
                    tvArea.setText(data.getArea() + "m²");
                    tvDeposit.setText(String.format("%,dđ", data.getHouseDeposit()).replace(",", "."));
                    tvCost.setText(String.format("%,dđ/tháng", data.getCost()).replace(",", "."));
//                    tvDetailInfo.setText(data.getDetailInfo());

                    // Load ảnh vào ViewPager2
                    List<ImageItem> imageItems = data.getImages();
                    String[] imageUrls = new String[imageItems.size()];
                    for (int i = 0; i < imageItems.size(); i++) {
                        imageUrls[i] = ApiConfig.Base_url+ "uploads/images/" +imageItems.get(i).getImageUrl();
                    }
                    ImageSliderAdapter adapter = new ImageSliderAdapter(imageUrls);
                    viewPager.setAdapter(adapter);

                    // Hiển thị tiện ích
                    layoutAmenities.removeAllViews();
                    for (Amenity amenity : data.getAmenities()) {
                        TextView tvAmenity = new TextView(roomDetailActivity.this);
                        tvAmenity.setText(amenity.getAmenityName() + ": " + String.format("%,dđ", amenity.getPricePerUnit()).replace(",", ".")+"/"+ amenity.getAmenityUnit());
                        tvAmenity.setTextSize(14);
                        tvAmenity.setPadding(16, 8, 16, 8);
                        tvAmenity.setBackgroundResource(android.R.drawable.editbox_background);
                        layoutAmenities.addView(tvAmenity);
                    }

                } else {
                    // Xử lý khi response lỗi hoặc body null
                    Toast.makeText(roomDetailActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApartmentResponse> call, Throwable t) {
                // Xử lý lỗi mạng hoặc lỗi khác
                Toast.makeText(roomDetailActivity.this, "Không thể kết nối tới server", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}