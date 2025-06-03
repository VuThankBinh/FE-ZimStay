package com.datn.zimstay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.adapter.AppointmentAdapter;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.api.ApiService;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.model.Appointment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentFragment extends Fragment {
    private static final String TAG = "AppointmentFragment";
    private RecyclerView rvAppointments;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;
    private int currentUserId;
    private Spinner spinnerStatus;
    private List<Appointment> allAppointments = new ArrayList<>(); // Lưu toàn bộ lịch hẹn

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);
        
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        rvAppointments = view.findViewById(R.id.rvAppointments);
        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(getContext(), appointmentList, new AppointmentAdapter.OnAppointmentChangedListener() {
            @Override
            public void onAppointmentChanged() {
                loadAppointments();
            }
        });
        
        rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAppointments.setAdapter(adapter);

        // Thiết lập spinner
        String[] statusArr = {"Chưa xác nhận", "Đã xác nhận", "Đã hủy"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, statusArr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(spinnerAdapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String status = "PENDING";
                if (position == 1) status = "CONFIRMED";
                else if (position == 2) status = "REJECTED";
                filterAppointments(status);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ZimStayPrefs", 0);
        currentUserId = sharedPreferences.getInt("nguoi_dung_id", 0);

        // Gọi API lấy danh sách lịch hẹn
        loadAppointments();

        return view;
    }

    private void loadAppointments() {
        Log.d(TAG, "loadAppointments");
        RetrofitClient
                .getInstance()
                .getApi()
                .getAppointmentsByOwner(currentUserId)
                .enqueue(new Callback<List<Appointment>>() {
                    @Override
                    public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            allAppointments.clear();
                            allAppointments.addAll(response.body());
                            // Lọc theo trạng thái đang chọn
                            int pos = spinnerStatus.getSelectedItemPosition();
                            String status = "PENDING";
                            if (pos == 1) status = "CONFIRMED";
                            else if (pos == 2) status = "REJECTED";
                            filterAppointments(status);
                        } else {
                            Log.e(TAG, "Error response: " + response.code());
                            Toast.makeText(getContext(), "Không thể tải danh sách lịch hẹn", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Appointment>> call, Throwable t) {
                        Log.e(TAG, "API call failed", t);
                        Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterAppointments(String status) {
        appointmentList.clear();
        for (Appointment a : allAppointments) {
            if (a.getStatus().equalsIgnoreCase(status)) {
                appointmentList.add(a);
            }
        }
        adapter.setStatusFilter(status);
        adapter.notifyDataSetChanged();
    }
} 