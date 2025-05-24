package com.datn.zimstay;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.GetTinhResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class searchActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    private List<String> districtList = new ArrayList<>();
    Spinner spinnerCity, spinnerDistrict;

    private List<String> cityNameList = new ArrayList<>();
    private List<String> cityIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        bottomNavigationView = findViewById(R.id.navView);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(searchActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_message) {
                    startActivity(new Intent(searchActivity.this, messageActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_search) {
                    // Nếu đang ở đây rồi thì không làm gì
                    return true;
                } else if (itemId == R.id.nav_notification) {
                    startActivity(new Intent(searchActivity.this, notificationActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(searchActivity.this, profileActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);

        getCityList();

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCityId = cityIdList.get(position);
                System.out.println("city id: " + selectedCityId);

                if (!selectedCityId.equals("-1")) {
                    fetchDistricts(selectedCityId); // Truyền đúng ID thành phố đã format vào API
                } else {
                    // Nếu là "Chọn thành phố" thì xóa districtList, thêm mặc định
                    districtList.clear();
                    districtList.add("Chọn quận/huyện");
                    ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(searchActivity.this,
                            android.R.layout.simple_spinner_item, districtList);
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDistrict.setAdapter(districtAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });

        // Nếu bạn có nút tìm kiếm, ví dụ buttonSearch, xử lý kiểm tra dưới đây
        findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateSelection()) {
                    // Tiến hành tìm kiếm
                    Toast.makeText(searchActivity.this, "Tìm kiếm thành công!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateSelection() {
        int cityPos = spinnerCity.getSelectedItemPosition();
        int districtPos = spinnerDistrict.getSelectedItemPosition();

        if (cityPos == 0) {
            Toast.makeText(this, "Vui lòng chọn thành phố", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (districtPos == 0) {
            Toast.makeText(this, "Vui lòng chọn quận/huyện", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getCityList() {
        RetrofitClient.getInstance()
                .getApi()
                .getTinh()
                .enqueue(new Callback<ArrayList<GetTinhResponse>>() {
                    @Override
                    public void onResponse(Call<ArrayList<GetTinhResponse>> call, Response<ArrayList<GetTinhResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            cityNameList.clear();
                            cityIdList.clear();

                            cityNameList.add("Chọn thành phố");
                            cityIdList.add("-1");

                            for (GetTinhResponse tinh : response.body()) {
                                cityNameList.add(tinh.getName());
                                // Format id thành "01", "02", ..., "10", "11", ...
                                cityIdList.add(String.format("%02d", tinh.getId()));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(searchActivity.this,
                                    android.R.layout.simple_spinner_item, cityNameList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCity.setAdapter(adapter);

                            // Thiết lập spinnerDistrict mặc định
                            districtList.clear();
                            districtList.add("Chọn quận/huyện");
                            ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(searchActivity.this,
                                    android.R.layout.simple_spinner_item, districtList);
                            districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerDistrict.setAdapter(districtAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<GetTinhResponse>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void fetchDistricts(String cityId) {
        RetrofitClient.getInstance().getApi().getQuanHuyen(cityId)
                .enqueue(new Callback<ArrayList<GetTinhResponse>>() {
                    @Override
                    public void onResponse(Call<ArrayList<GetTinhResponse>> call, Response<ArrayList<GetTinhResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            districtList.clear();
                            districtList.add("Chọn quận/huyện");
                            for (GetTinhResponse district : response.body()) {
                                districtList.add(district.getName());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(searchActivity.this,
                                    android.R.layout.simple_spinner_item, districtList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerDistrict.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<GetTinhResponse>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }
}
