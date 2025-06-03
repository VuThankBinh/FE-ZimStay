package com.datn.zimstay.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.R;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.ApartmentData;
import com.datn.zimstay.api.models.ApartmentResponse;
import com.datn.zimstay.api.models.TokenCheckResponse;
import com.datn.zimstay.model.Appointment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private Context context;
    private List<Appointment> appointments;
    private OnAppointmentChangedListener listener;
    private String statusFilter = "PENDING";

    public interface OnAppointmentChangedListener {
        void onAppointmentChanged();
    }

    public AppointmentAdapter(Context context, List<Appointment> appointments, OnAppointmentChangedListener listener) {
        this.context = context;
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);

        holder.tvDate.setText("Ngày: " + appointment.getAppointmentDate());
        holder.tvTime.setText("Giờ: " + appointment.getAppointmentTime());
        holder.tvPhone.setText("SĐT: " + appointment.getPhone());
        holder.tvStatus.setText("Trạng thái: " + appointment.getStatus());

        // Load tên người đặt
        getName(appointment.getOwnerId(), name -> holder.tvName.setText("Người đặt: " + name));

        // Load địa chỉ căn hộ
        getAddress(appointment.getApartmentId(), address -> holder.tvAddress.setText("Đc phòng trọ: " + address));

        // Xử lý sự kiện click nút nếu cần
        holder.btnConfirm.setOnClickListener(v -> {
            // TODO: xử lý xác nhận lịch hẹn
            updateAppointment(appointment.getId(), "CONFIRMED", appointment.getRenterId(), "Lịch hẹn của bạn đã được chấp nhận",
                    "Lịch hẹn của bạn với chủ phòng trọ"
                            +"\n"+  holder.tvAddress.getText().toString()
                            +"\n"+" Vào: "+appointment.getAppointmentTime() +" \nNgày: "+ appointment.getAppointmentDate()+"\n"+"đã được xác nhận");
        });

        holder.btnCancel.setOnClickListener(v -> {
            // TODO: xử lý hủy lịch hẹn
            updateAppointment(appointment.getId(), "REJECTED", appointment.getRenterId(), "Lịch hẹn của bạn đã bị hủy",
                    "Lịch hẹn của bạn với chủ phòng trọ"
                            +"\n "+  holder.tvAddress.getText().toString()
                            +"\n"+" Vào: "+appointment.getAppointmentTime() +" \nNgày: "+ appointment.getAppointmentDate()+"\n"+"đã bị hủy");

        });

        if (!statusFilter.equalsIgnoreCase("PENDING")) {
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        } else {
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }
    private void updateAppointment(int appointmentId, String status, int userID, String title, String body){
        System.out.println("uri: "+ RetrofitClient
                .getInstance()
                .getApi()
                .updateStatusAppointment(appointmentId, status).request().url());
        RetrofitClient
                .getInstance()
                .getApi()
                .updateStatusAppointment(appointmentId, status)
                .enqueue(new Callback<Appointment>() {
                    @Override
                    public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                        if(response.isSuccessful() && response.body() != null)
                        {
                            Appointment appointment = response.body();
                            sendNotification(userID, title, body);
                            if(status.equals("CONFIRMED"))
                                Toast.makeText(context, "Xác nhận lịch hẹn thành công", Toast.LENGTH_SHORT).show();
                            if(status.equals("REJECTED"))
                                Toast.makeText(context, "Hủy lịch hẹn thành công", Toast.LENGTH_SHORT).show();
                            
                            // Gọi lại API lấy danh sách lịch hẹn
                            if(listener != null) {
                                listener.onAppointmentChanged();
                            }
                        }
                        else {
                            try {
                                String errorBody = response.errorBody().string();
                                JSONObject errorObject = new JSONObject(errorBody);
                                String errorMessage = errorObject.optString("error", "Lỗi không xác định");
                                Toast.makeText(context, "Giờ hẹn không còn phù hợp", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Có lỗi xảy ra khi xử lý phản hồi từ máy chủ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Appointment> call, Throwable t) {
                        Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private void sendNotification(int userId, String title, String body) {
        // Lấy thông tin căn hộ
        JsonObject notificationData = new JsonObject();
        notificationData.addProperty("userId", userId);
        notificationData.addProperty("title", title);
        notificationData.addProperty("body", body);
        notificationData.addProperty("image", "https://media.canva.com/v2/image-resize/format:JPG/height:1024/quality:92/uri:ifs%3A%2F%2FM%2Fb9ef4f6789a4489ca312df2dd3d8a1fa/watermark:F/width:1024?csig=AAAAAAAAAAAAAAAAAAAAANqVPuf-9zdpVwjVybsTX9b1hxa73eCYWES393_4iiRa&exp=1748467852&osig=AAAAAAAAAAAAAAAAAAAAANjDCjruF8xa0uyeGjwqzh29LmzV_VEiQ4_ONJR-cvx9&signer=media-rpc&x-canva-quality=screen_2x");
        Gson gson =new Gson();
        // Gửi thông báo
        RequestBody body1 = RequestBody.create(JSON, gson.toJson(notificationData));
        Request request = new Request.Builder()
                .url(ApiConfig.Base_url + "api/notifications/payment")
                .post(body1)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.out.println("Lỗi gửi thông báo: " + response.code());
                }
            }
        });
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime, tvPhone, tvStatus, tvName, tvAddress;
        Button btnConfirm, btnCancel;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvStatus = itemView.findViewById(R.id.tvStatus2);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }

    // Giao diện callback đơn giản
    private interface OnDataLoadedCallback {
        void onDataLoaded(String data);
    }

    private void getName(int userId, OnDataLoadedCallback callback) {
        RetrofitClient.getInstance().getApi().getUserById(userId)
                .enqueue(new Callback<TokenCheckResponse>() {
                    @Override
                    public void onResponse(Call<TokenCheckResponse> call, Response<TokenCheckResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String name = response.body().getData().getUserName();
                            callback.onDataLoaded(name);
                        } else {
                            callback.onDataLoaded("Không xác định");
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenCheckResponse> call, Throwable t) {
                        callback.onDataLoaded("Lỗi mạng");
                    }
                });
    }

    private void getAddress(int apartmentId, OnDataLoadedCallback callback) {
        RetrofitClient.getInstance().getApi().getApartmentById(apartmentId)
                .enqueue(new Callback<ApartmentResponse>() {
                    @Override
                    public void onResponse(Call<ApartmentResponse> call, Response<ApartmentResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApartmentData data = response.body().getData();
                            String address = data.getAddress() + ", " + data.getWard() + ", " + data.getDistrict() + ", " + data.getCity();
                            callback.onDataLoaded(address);
                        } else {
                            callback.onDataLoaded("Không xác định");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApartmentResponse> call, Throwable t) {
                        callback.onDataLoaded("Lỗi mạng");
                    }
                });
    }

    public void setStatusFilter(String status) {
        this.statusFilter = status;
        notifyDataSetChanged();
    }
}
