package com.datn.zimstay;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datn.zimstay.adapter.ApartmentAdapter;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.model.Apartment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.databinding.ActivityMainBinding;
import com.datn.zimstay.model.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LinearLayout citiesContainer;
    private OkHttpClient client;
    EditText TimKiem;
    TextView anHien;
    private Gson gson;
    private RecyclerView rvCheapest;
    private ApartmentAdapter apartmentAdapter;
    private List<Apartment> apartmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Khởi tạo OkHttpClient và Gson
        client = new OkHttpClient();
        gson = new Gson();
        
        // Tìm view container cho danh sách thành phố
        citiesContainer = findViewById(R.id.citiesContainer);
        TimKiem=findViewById(R.id.TimKiem);
        anHien=findViewById(R.id.anHien);
        TimKiem.setOnClickListener(View ->{
            Intent intent = new Intent(MainActivity.this, searchActivity.class);
            startActivity(intent);
        });


        // Gọi API lấy danh sách thành phố
        fetchTopCities();

        bottomNavigationView = findViewById(R.id.navView);
        // Thiết lập item được chọn là Practice
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    return true;
                } else if (itemId == R.id.nav_message) {
                    Intent intent = new Intent(MainActivity.this, messageActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_search) {
                    Intent intent = new Intent(MainActivity.this, searchActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                } else if (itemId == R.id.nav_notification) {
                    Intent intent = new Intent(MainActivity.this, notificationActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(MainActivity.this, profileActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
        rvCheapest = findViewById(R.id.recyclerView); // Đổi thành id đúng của RecyclerView phòng giá rẻ
        rvCheapest.setLayoutManager(new LinearLayoutManager(this));
        apartmentAdapter = new ApartmentAdapter(this, apartmentList, new ApartmentAdapter.OnApartmentClickListener() {
            @Override
            public void onApartmentClick(int apartmentId) {
                Intent intent = new Intent(MainActivity.this, roomDetailActivity.class);
                intent.putExtra("apartment_id", apartmentId);
                startActivity(intent);
            }
        });
        rvCheapest.setAdapter(apartmentAdapter);
        anHien.setOnClickListener(View ->{
            if(anHien.getText().equals("Ẩn đi")){
                anHien.setText("Hiện ra");
                rvCheapest.setVisibility(View.GONE);

            }else{
                anHien.setText("Ẩn đi");
                rvCheapest.setVisibility(View.VISIBLE);
            }
        });
        fetchCheapestApartments();
    }

    private void fetchTopCities() {
        Request request = new Request.Builder()
                .url(ApiConfig.Base_url+"api/apartments/top-cities")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    Type listType = new TypeToken<ArrayList<City>>(){}.getType();
                    List<City> cities = gson.fromJson(jsonData, listType);
                    
                    runOnUiThread(() -> {
                        displayCities(cities);
                    });
                }
            }
        });
    }
    private void fetchCheapestApartments() {
        Request request = new Request.Builder()
                .url(ApiConfig.Base_url+"api/apartments/top-cheapest")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        Type listType = new TypeToken<ArrayList<Apartment>>(){}.getType();
                        List<Apartment> apartments = gson.fromJson(dataArray.toString(), listType);

                        runOnUiThread(() -> {
                            apartmentList.clear();
                            apartmentList.addAll(apartments);
                            apartmentAdapter.notifyDataSetChanged();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void displayCities(List<City> cities) {
        citiesContainer.removeAllViews();
        
        LinearLayout rowLayout = null;
        for (int i = 0; i < cities.size(); i++) {
            if (i % 3 == 0) {
                rowLayout = new LinearLayout(this);
                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                citiesContainer.addView(rowLayout);
            }

            City city = cities.get(i);
            LinearLayout cityLayout = new LinearLayout(this);
            cityLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            cityLayout.setOrientation(LinearLayout.VERTICAL);
            cityLayout.setPadding(20, 20, 20, 20);
            cityLayout.setBackgroundResource(R.drawable.botron_img);
            cityLayout.setGravity(android.view.Gravity.CENTER);

            // Thêm ImageView cho thành phố
            android.widget.ImageView imageView = new android.widget.ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(300, 250));
            imageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundResource(R.drawable.botron_img);
            imageView.setPadding(10, 10, 10, 10);
            ArrayList<Integer> images = new ArrayList();
            images.add(R.drawable.img_trangchu1);
            images.add(R.drawable.img_trangchu2);
            images.add(R.drawable.img_trangchu3);
            images.add(R.drawable.img_trangchu4);
            images.add(R.drawable.img_trangchu5);
            images.add(R.drawable.img_trangchu6);
            imageView.setImageResource(images.get(i));
//            // TODO: Thêm ảnh tương ứng cho từng thành phố
//            imageView.setImageResource(R.drawable.img_trangchu1);
            
            // Thêm TextView hiển thị tên thành phố
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText(city.getCity());
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(18);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setGravity(android.view.Gravity.CENTER);

            cityLayout.addView(imageView);
            cityLayout.addView(textView);
            rowLayout.addView(cityLayout);
        }
    }
}