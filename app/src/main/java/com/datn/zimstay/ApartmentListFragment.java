package com.datn.zimstay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.adapter.ApartmentStatusAdapter;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.api.ApiService;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.ApartmentsResponse;
import com.datn.zimstay.api.models.ApiResponse;
import com.datn.zimstay.api.models.ApartmentStatusResponse;
import com.datn.zimstay.model.Apartment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ApartmentListFragment extends Fragment {
    private static final String TAG = "ApartmentListFragment";
    private RecyclerView rvApartments;
    private ApartmentStatusAdapter adapter;
    private List<Apartment> apartmentList;
    private List<Apartment> allApartments = new ArrayList<>();
    private int currentUserId;
    private Spinner spinnerStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_apartment_list, container, false);
        
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        rvApartments = view.findViewById(R.id.rvApartments);
        Button fabAddApartment = view.findViewById(R.id.btnAdd);
        
        // Xử lý sự kiện click nút thêm mới
        fabAddApartment.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), chuc_nang_quan_ly_tro.class);
            startActivity(intent);
        });

        apartmentList = new ArrayList<>();
        adapter = new ApartmentStatusAdapter(requireContext(), apartmentList, new ApartmentStatusAdapter.OnApartmentClickListener() {
            @Override
            public void onApartmentClick(int apartmentId) {
                // Xử lý khi click vào item
                navigateToApartmentDetail(apartmentId);
            }

            @Override
            public void onEditStatusClick(int apartmentId, String currentStatus) {
                // Hiển thị dialog để chọn trạng thái mới
                showStatusDialog(apartmentId, currentStatus);
            }

            @Override
            public void onEditInfoClick(int apartmentId) {
                // Chuyển đến màn hình chỉnh sửa thông tin
                navigateToEditApartment(apartmentId);
            }
        });
        
        rvApartments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvApartments.setAdapter(adapter);

        // Thiết lập spinner
        String[] statusArr = {"Tất cả", "Còn trống", "Đã cho thuê"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, statusArr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(spinnerAdapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterApartments(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ZimStayPrefs", 0);
        currentUserId = sharedPreferences.getInt("nguoi_dung_id", 0);

        // Gọi API lấy danh sách phòng trọ
        loadApartments();

        return view;
    }

    private void loadApartments() {
        Log.d(TAG, "loadApartments");
        String url = RetrofitClient.getInstance().getApi().getApartmentsByOwner(currentUserId).request().url().toString();
        Log.d(TAG, "API URL: " + url);
        
        RetrofitClient
                .getInstance()
                .getApi()
                .getApartmentsByOwner(currentUserId)
                .enqueue(new Callback<ApartmentsResponse>() {
                    @Override
                    public void onResponse(Call<ApartmentsResponse> call, Response<ApartmentsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Gson gson = new Gson();
                            String json = gson.toJson(response.body());
                            Log.d(TAG, "Response: " + json);
                            allApartments.clear();
                            allApartments.addAll(response.body().getData());
                            // Lọc theo trạng thái đang chọn
                            filterApartments(spinnerStatus.getSelectedItemPosition());
                        } else {
                            Log.e(TAG, "Error response: " + response.code());
                            Toast.makeText(getContext(), "Không thể tải danh sách phòng trọ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApartmentsResponse> call, Throwable t) {
                        Log.e(TAG, "API call failed", t);
                        Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterApartments(int position) {
        apartmentList.clear();
        for (Apartment apartment : allApartments) {
            switch (position) {
                case 0: // Tất cả
                    apartmentList.add(apartment);
                    break;
                case 1: // Còn trống
                    if (apartment.getStatus() == null || apartment.getStatus().equalsIgnoreCase("AVAILABLE")) {
                        apartmentList.add(apartment);
                    }
                    break;
                case 2: // Đã cho thuê
                    if (apartment.getStatus() != null && apartment.getStatus().equalsIgnoreCase("UNAVAILABLE")) {
                        apartmentList.add(apartment);
                    }
                    break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showStatusDialog(int apartmentId, String currentStatus) {
        String[] statuses = {"Còn trống", "Đã cho thuê"};
        int currentIndex = currentStatus != null && currentStatus.equalsIgnoreCase("AVAILABLE") ? 0 : 1;

        new AlertDialog.Builder(requireContext())
            .setTitle("Chọn trạng thái")
            .setSingleChoiceItems(statuses, currentIndex, null)
            .setPositiveButton("OK", (dialog, which) -> {
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                String newStatus = selectedPosition == 0 ? "AVAILABLE" : "UNAVAILABLE";
                updateApartmentStatus(apartmentId, newStatus);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void updateApartmentStatus(int apartmentId, String newStatus) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ZimStayPrefs", 0);
        String token = sharedPreferences.getString("token", null);
        
        RetrofitClient.getInstance()
            .getApi()
            .toggleApartmentStatus(apartmentId, "Bearer " + token)
            .enqueue(new Callback<ApartmentStatusResponse>() {
                @Override
                public void onResponse(Call<ApartmentStatusResponse> call, Response<ApartmentStatusResponse> response) {
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    System.out.println("Response: " + json);
                    System.out.println("response.isSuccessful(): " + response.isSuccessful());
                    System.out.println("response.body() != null: " + (response.body() != null));

                    if (response.isSuccessful() && response.body() != null) {
                        ApartmentStatusResponse apiResponse = response.body();

                        if (apiResponse.getStatus().equals("OK")) {
                            Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            // Cập nhật lại danh sách
                            loadApartments();
                        } else {
                            Toast.makeText(getContext(), "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Không thể cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApartmentStatusResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void navigateToEditApartment(int apartmentId) {
        // TODO: Chuyển đến màn hình chỉnh sửa thông tin
        // Có thể sử dụng Navigation component hoặc Intent
        Intent intent = new Intent(getActivity(), chuc_nang_quan_ly_tro.class);
        intent.putExtra("apartmentId", apartmentId);
        startActivity(intent);
    }

    private void navigateToApartmentDetail(int apartmentId) {
        // TODO: Chuyển đến màn hình chi tiết
        // Có thể sử dụng Navigation component hoặc Intent
    }

    @Override
    public void onResume() {
        super.onResume();
        loadApartments();
        filterApartments(spinnerStatus.getSelectedItemPosition());
    }
}