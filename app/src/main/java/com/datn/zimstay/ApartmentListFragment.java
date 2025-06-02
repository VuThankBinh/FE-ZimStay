package com.datn.zimstay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.adapter.ApartmentAdapter;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.api.ApiService;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.ApartmentsResponse;
import com.datn.zimstay.model.Apartment;
import com.datn.zimstay.model.Appointment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApartmentListFragment extends Fragment {
    private static final String TAG = "ApartmentListFragment";
    private RecyclerView rvApartments;
    private ApartmentAdapter adapter;
    private List<Apartment> apartmentList;
    private int currentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_apartment_list, container, false);
        
        rvApartments = view.findViewById(R.id.rvApartments);
        apartmentList = new ArrayList<>();
        adapter = new ApartmentAdapter(getContext(), apartmentList, apartmentId -> {
            // Xử lý khi click vào phòng trọ
        });
        
        rvApartments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvApartments.setAdapter(adapter);

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
                            apartmentList.clear();
                            apartmentList.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();
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
} 