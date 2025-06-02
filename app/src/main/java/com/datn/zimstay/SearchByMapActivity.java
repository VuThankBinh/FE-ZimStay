package com.datn.zimstay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.GetTinhResponse;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.location.Address;
import android.location.Geocoder;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.api.IMapController;
import java.io.IOException;
import java.util.Locale;

public class SearchByMapActivity extends AppCompatActivity {

    Spinner spinnerCity, spinnerDistrict, spinnerWard;
    EditText etDetail;
    Button btnShowMap;
    MapView mapView;

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
        setContentView(R.layout.activity_search_by_map);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        spinnerCity = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        etDetail = findViewById(R.id.etDetail);
        btnShowMap = findViewById(R.id.btnShowMap);
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(10);
        // Load city list
        getCityList();

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCityId = cityIdList.get(position);
                if (!selectedCityId.equals("-1")) {
                    fetchDistricts(selectedCityId);
                } else {
                    // Reset district and ward spinners
                    resetDistrictSpinner();
                    resetWardSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        spinnerWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedWardId = wardIdList.size() > position ? wardIdList.get(position) : "-1";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnShowMap.setOnClickListener(v -> {
            String detail = etDetail.getText().toString().trim();
            String city = spinnerCity.getSelectedItem() != null ? spinnerCity.getSelectedItem().toString() : "";
            String district = spinnerDistrict.getSelectedItem() != null ? spinnerDistrict.getSelectedItem().toString() : "";
            String ward = spinnerWard.getSelectedItem() != null ? spinnerWard.getSelectedItem().toString() : "";

            if (city.isEmpty() || district.isEmpty() || ward.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn đầy đủ địa chỉ", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullAddress = detail + ", " + ward + ", " + district + ", " + city +", Việt Nam";
            getLatLngFromAddress(SearchByMapActivity.this, fullAddress);
            new NominatimGeocodeTask().execute(fullAddress);
        });
        String[] addresses = new String[] {
                "92 Trần Phú, Đông Tảo, Huyện Khoái Châu, Hưng Yên, Việt Nam",
                "16 Nguyễn Chí Thanh, Bãi Sậy, Huyện Ân Thi, Hưng Yên, Việt Nam",
                "33 Hai Bà Trưng, Hạ Lễ, Huyện Ân Thi, Hưng Yên, Việt Nam",
                "64 Đinh Tiên Hoàng, Phạm Ngũ Lão, Huyện Kim Động, Hưng Yên, Việt Nam",
                "11 Tôn Đức Thắng, Chí Tân, Huyện Kim Động, Hưng Yên, Việt Nam",
                "10 Lê Thánh Tông, Tống Trân, Huyện Phù Cừ, Hưng Yên, Việt Nam",
                "12 Lê Duẩn, Điện Biên, Ba Đình, Hà Nội, Việt Nam",
                "89 Trần Phú, Văn Quán, Hà Đông, Hà Nội, Việt Nam",
                "123 Điện Biên Phủ, Phường 15, Quận Bình Thạnh, Hồ Chí Minh, Việt Nam",
                "23 Nguyễn Thị Minh Khai, Bến Nghé, Quận 1, Hồ Chí Minh, Việt Nam",
                "50 Nguyễn Văn Linh, Nam Dương, Hải Châu, Đà Nẵng, Việt Nam",
                "89 Lê Duẩn, Thanh Khê Đông, Thanh Khê, Đà Nẵng, Việt Nam",
                "17 Nguyễn Trãi, Tân An, Ninh Kiều, Cần Thơ, Việt Nam",
                "62 Phan Đình Phùng, An Cư, Ninh Kiều, Cần Thơ, Việt Nam",
                "88 Trần Nguyên Hãn, Trại Cau, Lê Chân, Hải Phòng, Việt Nam",
                "36 Tô Hiệu, Hàng Kênh, Lê Chân, Hải Phòng, Việt Nam",
                "123 Đường Nguyễn Văn Linh, Phường Tân Thuận Đông, Quận 7, Hồ Chí Minh, Việt Nam"
        };
        for (int i=0; i< addresses.length; i++){
            getLatLngFromAddress(SearchByMapActivity.this, addresses[i]);
        }


    }

    public static double[] getLatLngFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                System.out.println("address: "+address);
                System.out.println("getLongitude: "+location.getLongitude());
                System.out.println("getLatitude(): "+location.getLatitude());
                System.out.println("geography: "+ toWKT(location.getLongitude(), location.getLatitude()));
                System.out.println("----------------------------");
                return new double[]{location.getLatitude(), location.getLongitude()};
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // hoặc trả mảng rỗng nếu thất bại
    }
    private static String toWKT(double longitude, double latitude) {
        // WKT định dạng POINT(longitude latitude)
        return String.format("POINT(%f %f)", longitude, latitude);
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

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchByMapActivity.this,
                                    android.R.layout.simple_spinner_item, cityNameList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCity.setAdapter(adapter);

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

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchByMapActivity.this,
                                    android.R.layout.simple_spinner_item, districtList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerDistrict.setAdapter(adapter);

                            resetWardSpinner();
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
                            Gson gson = new Gson();
                            String json = gson.toJson(response.body());
                            System.out.println(json);

                            wardList.clear();
                            wardIdList.clear();
                            wardList.add("Chọn xã/phường");
                            wardIdList.add("-1");

                            for (GetTinhResponse ward : response.body()) {
                                wardList.add(ward.getName());
                                wardIdList.add(String.format("%02d", ward.getId()));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchByMapActivity.this,
                                    android.R.layout.simple_spinner_item, wardList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerWard.setAdapter(adapter);
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
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(SearchByMapActivity.this,
                android.R.layout.simple_spinner_item, districtList);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(districtAdapter);
    }

    private void resetWardSpinner() {
        wardList.clear();
        wardIdList.clear();
        wardList.add("Chọn xã/phường");
        wardIdList.add("-1");
        ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(SearchByMapActivity.this,
                android.R.layout.simple_spinner_item, wardList);
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWard.setAdapter(wardAdapter);
    }
    // ====== Nominatim Geocoding ======
    private class NominatimGeocodeTask extends AsyncTask<String, Void, GeoPoint> {
        @Override protected GeoPoint doInBackground(String... params) {
            try {
                String urlStr = "https://nominatim.openstreetmap.org/search?q=" +
                        URLEncoder.encode(params[0], "UTF-8") +
                        "&format=json&limit=1";
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
// Set a proper User-Agent string
                conn.setRequestProperty("User-Agent", "ZimStayApp/1.0 (contact: nguyendinhvuongabc1@gmail.com)");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
                reader.close();

                JSONArray array = new JSONArray(result.toString());
                if (array.length() > 0) {
                    JSONObject loc = array.getJSONObject(0);
                    double lat = loc.getDouble("lat");
                    double lon = loc.getDouble("lon");
                    return new GeoPoint(lat, lon);
                }
            } catch (Exception e) { e.printStackTrace(); }
            return null;
        }

        @Override protected void onPostExecute(GeoPoint geoPoint) {
            if (geoPoint != null) {
                mapView.getController().setZoom(16);
                mapView.getController().setCenter(geoPoint);
                mapView.getOverlays().clear();
                Marker marker = new Marker(mapView);
                marker.setPosition(geoPoint);
                marker.setTitle("Vị trí");
                mapView.getOverlays().add(marker);
                mapView.invalidate();
            } else {
                Toast.makeText(SearchByMapActivity.this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
