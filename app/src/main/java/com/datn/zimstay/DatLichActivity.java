package com.datn.zimstay;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.TokenCheckResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DatLichActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dat_lich);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        Intent intent = getIntent();
        int userId=intent.getIntExtra("userId",0);
        int apartmentId=intent.getIntExtra("apartment_id",0);
        int ownerId=intent.getIntExtra("ownerId",0);
        System.out.println("userIddl: "+userId);
        System.out.println("apartment_id: "+apartmentId);
        System.out.println("ownerId: "+ownerId);
        getUser2(ownerId);
        getUser1(userId);

        TextView textViewDate = findViewById(R.id.textViewDate);
        TextView textViewTime = findViewById(R.id.textViewTime);
        LinearLayout datePickerLayout = findViewById(R.id.datePickerLayout);
        LinearLayout timePickerLayout = findViewById(R.id.timePickerLayout);
        Button btnDatLich = findViewById(R.id.btnDatLich);

        Locale localeVN = new Locale("vi");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", localeVN);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", localeVN);

// Mặc định ngày mai
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        textViewDate.setText(dateFormat.format(calendar.getTime()));
        AnhXa();
// Mặc định giờ hiện tại
        Calendar currentTime = Calendar.getInstance();
        textViewTime.setText(timeFormat.format(currentTime.getTime()));

// Sự kiện chọn ngày
        datePickerLayout.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            Calendar maxDay = Calendar.getInstance();
            maxDay.add(Calendar.DAY_OF_YEAR, 7);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    v.getContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, dayOfMonth);
                        textViewDate.setText(dateFormat.format(selected.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
            datePickerDialog.getDatePicker().setMaxDate(maxDay.getTimeInMillis());
            datePickerDialog.show();
        });

// Sự kiện chọn giờ
        timePickerLayout.setOnClickListener(v -> {
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    v.getContext(),
                    (view, selectedHour, selectedMinute) -> {
                        // Kiểm tra xem giờ có nằm trong khoảng 7h - 20h không
                        if (selectedHour >= 7 && selectedHour <= 20) {
                            Calendar selectedTime = Calendar.getInstance();
                            selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                            selectedTime.set(Calendar.MINUTE, selectedMinute);
                            textViewTime.setText(timeFormat.format(selectedTime.getTime()));
                        } else {
                            Toast.makeText(v.getContext(),
                                    "Vui lòng chọn giờ từ 07:00 đến 20:00",
                                    Toast.LENGTH_SHORT).show();
                        }
                    },
                    hour, minute, true // true = 24h format
            );
            timePickerDialog.show();
        });

        // Xử lý sự kiện đặt lịch
        btnDatLich.setOnClickListener(v -> {
            String date = textViewDate.getText().toString();
            String time = textViewTime.getText().toString();
            
            if (date.equals("Chọn ngày") || time.equals("Chọn giờ")) {
                Toast.makeText(this, "Vui lòng chọn ngày và giờ xem phòng", Toast.LENGTH_SHORT).show();
                return;
            }
            TextView phone1= findViewById(R.id.phone1);
            JsonObject appointmentData = new JsonObject();
            appointmentData.addProperty("apartmentId", apartmentId); // Cần lấy từ intent
            appointmentData.addProperty("renterId", userId); // Cần lấy từ user đang đăng nhập
            appointmentData.addProperty("ownerId", ownerId); // Cần lấy từ thông tin phòng
            appointmentData.addProperty("appointmentTime", time + ":00");
            appointmentData.addProperty("status", "chua_xac_nhan");
            appointmentData.addProperty("appointmentDate", date);
            appointmentData.addProperty("phone", phone1.getText().toString().trim());

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, gson.toJson(appointmentData));
                System.out.println("request: "+gson.toJson(appointmentData));
            Request request = new Request.Builder()
                    .url(ApiConfig.Base_url + "api/appointments")
                    .post(body)
                    .build();
            System.out.println("url:" + ApiConfig.Base_url + "/api/appointments");
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

// Ví dụ gọi API
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(DatLichActivity.this,
                            "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println("response: "+gson.toJson(response.body()));
                    System.out.println("code: "+ response.code());
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(DatLichActivity.this,
                                    "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DatLichActivity.this, DatLichThanhCongActivity.class);
                            intent.putExtra("userName", name1.getText().toString());
                            intent.putExtra("apartmentId", apartmentId);
                            intent.putExtra("ownerId", ownerId);
                            intent.putExtra("userId", userId);
                            intent.putExtra("appointmentDate", time+" "+date);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        ResponseBody errorBody = response.body(); // hoặc response.errorBody() nếu dùng Retrofit
                        String errorMessage = "Có lỗi xảy ra";
                        if (errorBody != null) {
                            try {
                                String json = errorBody.string(); // chỉ gọi 1 lần
                                JsonObject errorJson = gson.fromJson(json, JsonObject.class);
                                if (errorJson != null && errorJson.has("error")) {
                                    errorMessage = errorJson.get("error").getAsString();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                errorMessage = "Không thể đọc phản hồi lỗi từ server.";
                            }
                        }

                        String finalErrorMessage = errorMessage;
                        runOnUiThread(() -> Toast.makeText(DatLichActivity.this,
                                finalErrorMessage, Toast.LENGTH_SHORT).show());
                    }
                }
            });
        });

    }
    TextView phone1,phone2,name1,name2;
    ImageView back, user1,user2;
    private void AnhXa(){
        phone1=findViewById(R.id.phone1);
        phone2=findViewById(R.id.phone2);
        name1=findViewById(R.id.name1);
        name2=findViewById(R.id.name2);
        back=findViewById(R.id.back);
        user1=findViewById(R.id.user1);
        user2=findViewById(R.id.user2);
    }
    private void getUser1(int ownerId){
        RetrofitClient.getInstance().getApi().getUserById(ownerId)
                .enqueue(new retrofit2.Callback<TokenCheckResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<TokenCheckResponse> call, retrofit2.Response<TokenCheckResponse> response) {

                        Gson gson = new Gson();
                        System.out.println("userResponse: "+gson.toJson(response.body()));
                        if (response.isSuccessful() && response.body() != null) {
                            TokenCheckResponse userResponse = response.body();
                            // Hiển thị tên người dùng
                            phone1.setText(userResponse.getData().getPhoneNumber());
                            name1.setText(userResponse.getData().getUserName());
                            String imageUrl = ApiConfig.Base_url+"uploads/images/" +userResponse.getData().getAvatar();
                            Glide.with(DatLichActivity.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.user_1)
                                    .into(user1);


                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<TokenCheckResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }
    private void getUser2(int ownerId){
        RetrofitClient.getInstance().getApi().getUserById(ownerId)
                .enqueue(new retrofit2.Callback<TokenCheckResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<TokenCheckResponse> call, retrofit2.Response<TokenCheckResponse> response) {

                        Gson gson = new Gson();
                        System.out.println("userResponse: "+gson.toJson(response.body()));
                        if (response.isSuccessful() && response.body() != null) {
                            TokenCheckResponse userResponse = response.body();
                            // Hiển thị tên người dùng
                            phone2.setText(userResponse.getData().getPhoneNumber());
                            name2.setText(userResponse.getData().getUserName());
                            String imageUrl = ApiConfig.Base_url+"uploads/images/" +userResponse.getData().getAvatar();
                            Glide.with(DatLichActivity.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.user_1)
                                    .into(user2);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<TokenCheckResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }
}