package com.datn.zimstay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.api.ApiService;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.Amenity;
import com.datn.zimstay.api.models.ApartmentData;
import com.datn.zimstay.api.models.ApartmentResponse;
import com.datn.zimstay.api.models.GetTinhResponse;
import com.datn.zimstay.api.models.ImageItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class chuc_nang_quan_ly_tro extends AppCompatActivity {

    private static final String TAG = "chuc_nang_quan_ly_tro";
    private int apartmentId;
    private ApartmentData apartment;
    SharedPreferences sharedPreferences;
    EditText etDien, etNuoc, etInternet, etGiuXe, etAnNinh, etAddress, etCost, etArea, etHouseDeposit, etLocation;
    CheckBox cbNuoiThuCung, cbMayLanh, cbMayGiat, cbChoDeXe, cbNhaTamRieng, cbXeDien;
    ImageView imgPreview;
    Spinner spCity, spDistrict, spWard;

    private List<String> cityNameList = new ArrayList<>();
    private List<String> cityIdList = new ArrayList<>();
    private List<String> districtList = new ArrayList<>();
    private List<String> districtIdList = new ArrayList<>();
    private List<String> wardList = new ArrayList<>();
    private List<String> wardIdList = new ArrayList<>();

    private String selectedCityId = "-1";
    private String selectedDistrictId = "-1";
    private String selectedWardId = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chuc_nang_quan_ly_tro);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        Intent intent = getIntent();
        apartmentId = intent.getIntExtra("apartmentId", -1);
        sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);

        initViews();
        setupSpinnerListeners();
        getCityList();

        if(apartmentId != -1){
            getApartmentDetail(apartmentId);
        }
    }

    private void initViews() {
        etDien = findViewById(R.id.etDien);
        etNuoc = findViewById(R.id.etNuoc);
        etInternet = findViewById(R.id.etInternet);
        etGiuXe = findViewById(R.id.etGiuXe);
        etAnNinh = findViewById(R.id.etAnNinh);
        etAddress = findViewById(R.id.dccuthe);
        etCost = findViewById(R.id.etCost);
        etArea = findViewById(R.id.etArea);
        etHouseDeposit = findViewById(R.id.etHouseDeposit);
        etLocation = findViewById(R.id.etLocation);
        cbNuoiThuCung = findViewById(R.id.cbNuoiThuCung);
        cbMayLanh = findViewById(R.id.cbMayLanh);
        cbMayGiat = findViewById(R.id.cbMayGiat);
        cbChoDeXe = findViewById(R.id.cbChoDeXe);
        cbNhaTamRieng = findViewById(R.id.cbNhaTamRieng);
        cbXeDien = findViewById(R.id.cbXeDien);
        imgPreview = findViewById(R.id.imgPreview);
        spCity = findViewById(R.id.spCity);
        spDistrict = findViewById(R.id.spDistrict);
        spWard = findViewById(R.id.spWard);
    }

    private void setupSpinnerListeners() {
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCityId = cityIdList.get(position);
                if (!selectedCityId.equals("-1")) {
                    fetchDistricts(selectedCityId);
                } else {
                    resetDistrictSpinner();
                    resetWardSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDistrictId = districtIdList.size() > position ? districtIdList.get(position) : "-1";
                if (!selectedDistrictId.equals("-1") && !selectedCityId.equals("-1")) {
                    fetchWards(selectedDistrictId, selectedCityId);
                } else {
                    resetWardSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedWardId = wardIdList.size() > position ? wardIdList.get(position) : "-1";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(chuc_nang_quan_ly_tro.this,
                                    android.R.layout.simple_spinner_item, cityNameList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spCity.setAdapter(adapter);

                            resetDistrictSpinner();
                            resetWardSpinner();
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
                            districtIdList.clear();
                            districtList.add("Chọn quận/huyện");
                            districtIdList.add("-1");

                            for (GetTinhResponse district : response.body()) {
                                districtList.add(district.getName());
                                districtIdList.add(String.format("%02d", district.getId()));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(chuc_nang_quan_ly_tro.this,
                                    android.R.layout.simple_spinner_item, districtList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDistrict.setAdapter(adapter);

                            // Tìm vị trí của quận/huyện trong danh sách
                            int districtPosition = districtList.indexOf(apartment.getDistrict());
                            if (districtPosition != -1) {
                                spDistrict.setSelection(districtPosition);
                                selectedDistrictId = districtIdList.get(districtPosition);
                                // Load xã/phường
                                fetchWards(selectedDistrictId, cityId);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<GetTinhResponse>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void fetchWards(String districtId, String cityId) {
        RetrofitClient.getInstance().getApi().getXa(cityId, districtId)
                .enqueue(new Callback<ArrayList<GetTinhResponse>>() {
                    @Override
                    public void onResponse(Call<ArrayList<GetTinhResponse>> call, Response<ArrayList<GetTinhResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            wardList.clear();
                            wardIdList.clear();
                            wardList.add("Chọn xã/phường");
                            wardIdList.add("-1");

                            for (GetTinhResponse ward : response.body()) {
                                wardList.add(ward.getName());
                                wardIdList.add(String.format("%02d", ward.getId()));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(chuc_nang_quan_ly_tro.this,
                                    android.R.layout.simple_spinner_item, wardList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spWard.setAdapter(adapter);

                            // Tìm vị trí của xã/phường trong danh sách
                            int wardPosition = wardList.indexOf(apartment.getWard());
                            if (wardPosition != -1) {
                                spWard.setSelection(wardPosition);
                                selectedWardId = wardIdList.get(wardPosition);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<GetTinhResponse>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void resetDistrictSpinner() {
        districtList.clear();
        districtIdList.clear();
        districtList.add("Chọn quận/huyện");
        districtIdList.add("-1");
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(chuc_nang_quan_ly_tro.this,
                android.R.layout.simple_spinner_item, districtList);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistrict.setAdapter(districtAdapter);
    }

    private void resetWardSpinner() {
        wardList.clear();
        wardIdList.clear();
        wardList.add("Chọn xã/phường");
        wardIdList.add("-1");
        ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(chuc_nang_quan_ly_tro.this,
                android.R.layout.simple_spinner_item, wardList);
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWard.setAdapter(wardAdapter);
    }

    private void getApartmentDetail(int id) {
        ApiService apiService = RetrofitClient.getInstance().getApi();
        Call<ApartmentResponse> call = apiService.getApartmentById(id);
        call.enqueue(new Callback<ApartmentResponse>() {
            @Override
            public void onResponse(Call<ApartmentResponse> call, Response<ApartmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApartmentResponse apartmentResponse = response.body();
                    apartment = apartmentResponse.getData();

                    // Các trường thông tin khác
                    etAddress.setText(apartment.getAddress());
                    etCost.setText(String.valueOf(apartment.getCost()));
                    etArea.setText(String.valueOf(apartment.getArea()));
                    etHouseDeposit.setText(String.valueOf(apartment.getHouseDeposit()));
                    etLocation.setText(apartment.getLocation());

                    // Đổ dữ liệu tiện ích vào EditText/CheckBox
                    if (apartment.getAmenities() != null) {
                        for (Amenity amenity : apartment.getAmenities()) {
                            switch (amenity.getAmenityId()) {
                                case 1: etDien.setText(String.valueOf(amenity.getPricePerUnit())); break;
                                case 2: etNuoc.setText(String.valueOf(amenity.getPricePerUnit())); break;
                                case 3: etInternet.setText(String.valueOf(amenity.getPricePerUnit())); break;
                                case 4: etGiuXe.setText(String.valueOf(amenity.getPricePerUnit())); break;
                                case 5: etAnNinh.setText(String.valueOf(amenity.getPricePerUnit())); break;
                                case 7: cbNuoiThuCung.setChecked(true); break;
                                case 8: cbMayLanh.setChecked(true); break;
                                case 9: cbMayGiat.setChecked(true); break;
                                case 10: cbChoDeXe.setChecked(true); break;
                                case 11: cbNhaTamRieng.setChecked(true); break;
                                case 12: cbXeDien.setChecked(true); break;
                            }
                        }
                    }

                    // Hiển thị ảnh đầu tiên (nếu có)
                    if (apartment.getImages() != null && !apartment.getImages().isEmpty()) {
                        String url = ApiConfig.Base_url + "uploads/images/" + apartment.getImages().get(0).getImageUrl();
                        Glide.with(chuc_nang_quan_ly_tro.this).load(url).into(imgPreview);
                    }

                    // Set địa chỉ vào spinner
                    if (apartment.getCity() != null && apartment.getDistrict() != null && apartment.getWard() != null) {
                        // Đợi danh sách tỉnh/thành phố được load xong
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

                                            // Tìm vị trí của thành phố trong danh sách
                                            int cityPosition = cityNameList.indexOf(apartment.getCity());
                                            if (cityPosition != -1) {
                                                spCity.setSelection(cityPosition);
                                                selectedCityId = cityIdList.get(cityPosition);
                                                // Load quận/huyện
                                                fetchDistricts(selectedCityId, apartment.getDistrict(), apartment.getWard());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ArrayList<GetTinhResponse>> call, Throwable t) {
                                        t.printStackTrace();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(chuc_nang_quan_ly_tro.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApartmentResponse> call, Throwable t) {
                Toast.makeText(chuc_nang_quan_ly_tro.this, "Không thể kết nối tới server", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void fetchDistricts(String cityId, String districtName, String wardName) {
        RetrofitClient.getInstance().getApi().getQuanHuyen(cityId)
                .enqueue(new Callback<ArrayList<GetTinhResponse>>() {
                    @Override
                    public void onResponse(Call<ArrayList<GetTinhResponse>> call, Response<ArrayList<GetTinhResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            districtList.clear();
                            districtIdList.clear();
                            districtList.add("Chọn quận/huyện");
                            districtIdList.add("-1");

                            for (GetTinhResponse district : response.body()) {
                                districtList.add(district.getName());
                                districtIdList.add(String.format("%02d", district.getId()));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(chuc_nang_quan_ly_tro.this,
                                    android.R.layout.simple_spinner_item, districtList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spDistrict.setAdapter(adapter);

                            // Tìm vị trí của quận/huyện trong danh sách
                            int districtPosition = districtList.indexOf(districtName);
                            if (districtPosition != -1) {
                                spDistrict.setSelection(districtPosition);
                                selectedDistrictId = districtIdList.get(districtPosition);
                                // Load xã/phường
                                fetchWards(selectedDistrictId, cityId, wardName);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<GetTinhResponse>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void fetchWards(String districtId, String cityId, String wardName) {
        RetrofitClient.getInstance().getApi().getXa(cityId, districtId)
                .enqueue(new Callback<ArrayList<GetTinhResponse>>() {
                    @Override
                    public void onResponse(Call<ArrayList<GetTinhResponse>> call, Response<ArrayList<GetTinhResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            wardList.clear();
                            wardIdList.clear();
                            wardList.add("Chọn xã/phường");
                            wardIdList.add("-1");

                            for (GetTinhResponse ward : response.body()) {
                                wardList.add(ward.getName());
                                wardIdList.add(String.format("%02d", ward.getId()));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(chuc_nang_quan_ly_tro.this,
                                    android.R.layout.simple_spinner_item, wardList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spWard.setAdapter(adapter);

                            // Tìm vị trí của xã/phường trong danh sách
                            int wardPosition = wardList.indexOf(wardName);
                            if (wardPosition != -1) {
                                spWard.setSelection(wardPosition);
                                selectedWardId = wardIdList.get(wardPosition);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<GetTinhResponse>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void setAddressToSpinners(String cityId, String districtId, String wardId) {
        // Set city
        int cityPosition = cityIdList.indexOf(cityId);
        if (cityPosition != -1) {
            spCity.setSelection(cityPosition);
            selectedCityId = cityId;
            fetchDistricts(cityId);
        }

        // Set district
        int districtPosition = districtIdList.indexOf(districtId);
        if (districtPosition != -1) {
            spDistrict.setSelection(districtPosition);
            selectedDistrictId = districtId;
            fetchWards(districtId, cityId);
        }

        // Set ward
        int wardPosition = wardIdList.indexOf(wardId);
        if (wardPosition != -1) {
            spWard.setSelection(wardPosition);
            selectedWardId = wardId;
        }
    }
}