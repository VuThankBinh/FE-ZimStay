package com.datn.zimstay;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.ApartmentResponse;
import com.datn.zimstay.api.models.ApartmentsResponse;
import com.datn.zimstay.api.models.GetTinhResponse;
import com.datn.zimstay.adapter.ApartmentAdapter;
import com.datn.zimstay.model.Apartment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class searchActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Spinner spinnerCity, spinnerDistrict;

    private List<String> cityNameList = new ArrayList<>();
    private List<String> cityIdList = new ArrayList<>();
    private List<String> districtList = new ArrayList<>();

    // Checkbox
    CheckBox checkboxPet, checkboxAC, checkboxLaundry, checkboxChoDeXe, checkboxNhaTam, checkboxElectricBike;

    private RecyclerView recyclerView2;
    private ApartmentAdapter apartmentAdapter;
    private List<Apartment> apartmentList = new ArrayList<>();
    private Integer minPrice = null;
    private Integer maxPrice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        AnhXa(); // Ánh xạ các checkbox

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
                    fetchDistricts(selectedCityId);
                } else {
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
            }
        });

        recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        apartmentAdapter = new ApartmentAdapter(this, apartmentList, new ApartmentAdapter.OnApartmentClickListener() {
            @Override
            public void onApartmentClick(int apartmentId) {
                Intent intent = new Intent(searchActivity.this, roomDetailActivity.class);
                intent.putExtra("apartment_id", apartmentId);
                startActivity(intent);
            }
        });
        recyclerView2.setAdapter(apartmentAdapter);

        findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateSelection()) {
                    // Lấy giá trị từ Spinner
                    String city = spinnerCity.getSelectedItem().toString();
                    String district = spinnerDistrict.getSelectedItem().toString();

                    // Làm sạch giá trị nếu là mặc định
                    if (city.equals("Chọn thành phố")) city = "";
                    if (district.equals("Chọn quận/huyện")) district = "";

                    // Xử lý chuỗi cắt bỏ tiền tố không cần thiết
                    city = city.replace("Thành phố", "")
                            .replace("Tỉnh", "")
                            .trim();

                    district = district.replace("Thành phố", "")
                            .replace("Quận", "")
                            .replace("Huyện", "")
                            .trim();

                    // Lấy tiện ích đã chọn
                    List<Integer> amenityIds = new ArrayList<>();
                    if (checkboxPet.isChecked()) amenityIds.add(7);
                    if (checkboxAC.isChecked()) amenityIds.add(8);
                    if (checkboxLaundry.isChecked()) amenityIds.add(9);
                    if (checkboxChoDeXe.isChecked()) amenityIds.add(10);
                    if (checkboxNhaTam.isChecked()) amenityIds.add(11);
                    if (checkboxElectricBike.isChecked()) amenityIds.add(12);

                    // Lấy khoảng giá từ RadioGroup
                    RadioGroup radioGroup = findViewById(R.id.priceRadioGroup);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if (selectedId == R.id.price_0_1) {
                        minPrice = 0;
                        maxPrice = 1000000;
                    } else if (selectedId == R.id.price_1_2) {
                        minPrice = 1000000;
                        maxPrice = 2000000;
                    } else if (selectedId == R.id.price_2_3) {
                        minPrice = 2000000;
                        maxPrice = 3000000;
                    } else if (selectedId == R.id.price_3_4) {
                        minPrice = 3000000;
                        maxPrice = 4000000;
                    } else if (selectedId == R.id.price_4_5) {
                        minPrice = 4000000;
                        maxPrice = 5000000;
                    } else if (selectedId == R.id.price_above_5) {
                        minPrice = 5000000;
                        maxPrice = 6000000;
                    }

                    System.out.println("minPrice: " + minPrice + ", maxPrice: " + maxPrice);

                    // Tạo JSON body để gọi API
                    JsonObject body = new JsonObject();
                    if (minPrice != null) body.addProperty("minPrice", minPrice);
                    if (maxPrice != null) body.addProperty("maxPrice", maxPrice);
                    if (!city.isEmpty()) body.addProperty("city", city);
                    body.add("district", JsonNull.INSTANCE);
//                    if (!district.isEmpty()) body.addProperty("district", district);

                    JsonArray amenitiesArray = new JsonArray();
                    for (int id : amenityIds) {
                        amenitiesArray.add(id);
                    }
                    body.add("amenityIds", amenitiesArray);
                    body.addProperty("status","AVAILABLE");

                    System.out.println("body: " + body); // debug log

                    // Gọi API tìm kiếm
                    searchApartments(body);
                }
            }
        });

    }

    private void AnhXa() {
        checkboxPet = findViewById(R.id.checkboxPet);
        checkboxAC = findViewById(R.id.checkboxAC);
        checkboxLaundry = findViewById(R.id.checkboxLaundry);
        checkboxChoDeXe = findViewById(R.id.checkboxChoDeXe);
        checkboxNhaTam = findViewById(R.id.checkboxNhaTam);
        checkboxElectricBike = findViewById(R.id.checkboxElectricBike);
    }

    private boolean validateSelection() {
        int cityPos = spinnerCity.getSelectedItemPosition();

        if (cityPos == 0) {
            Toast.makeText(this, "Vui lòng chọn thành phố", Toast.LENGTH_SHORT).show();
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
                                cityIdList.add(String.format("%02d", tinh.getId()));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(searchActivity.this,
                                    android.R.layout.simple_spinner_item, cityNameList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCity.setAdapter(adapter);

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

    private void searchApartments(JsonObject body) {
        RetrofitClient
                .getInstance()
                .getApi()
                .searchApartments(body)
                .enqueue(new Callback<ApartmentsResponse>() {
                    @Override
                    public void onResponse(Call<ApartmentsResponse> call, Response<ApartmentsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                ApartmentsResponse apartmentsResponse = response.body();
                                List<Apartment> apartments = apartmentsResponse.getData();

                                // In ra thông tin căn hộ bằng System.out.println
                                for (Apartment apartment : apartments) {
                                    System.out.println("ID: " + apartment.getId());
                                    System.out.println("Address: " + apartment.getAddress());
                                    System.out.println("City: " + apartment.getCity());
                                    System.out.println("District: " + apartment.getDistrict());
                                    System.out.println("Ward: " + apartment.getWard());
                                    System.out.println("Cost: " + apartment.getCost());
                                    System.out.println("Status: " + apartment.getStatus());
                                    System.out.println("Area: " + apartment.getArea());
                                    System.out.println("Deposit: " + apartment.getHouseDeposit());
                                    System.out.println("-----------");
                                }

                                apartmentList.clear();
                                apartmentList.addAll(apartments);
                                apartmentAdapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Response failed or body is null");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApartmentsResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }


}
