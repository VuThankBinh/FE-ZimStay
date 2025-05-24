package com.datn.zimstay;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.LoginRequest;
import com.datn.zimstay.api.models.LoginResponse;
import com.datn.zimstay.api.models.OtpRequest;
import com.datn.zimstay.api.models.OtpResponse;
import com.datn.zimstay.api.models.TokenCheckResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class loginActivity extends AppCompatActivity {
    private static final String TAG = "loginActivity";
    private static final String PREF_NAME = "ZimStayPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_AVATAR = "user_avatar";
    private static final String KEY_ACCOUNT_TYPE = "account_type";
    
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Ánh xạ các view
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);

        checkTokenExpiration();
        // Xử lý sự kiện click nút đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                
                if (validateInput(email, password)) {
                    loginUser(email, password);
                }
            }
        });

        // Xử lý sự kiện click quên mật khẩu
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etEmail.getText().toString().trim().isEmpty()){
                    Toast.makeText(loginActivity.this, "Bạn phải nhập email đã", Toast.LENGTH_SHORT).show();
                }
                sendOtp(etEmail.getText().toString().trim());
            }
        });

        // Xử lý sự kiện click đăng ký
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, registerActivity.class);
                startActivity(intent);
            }
        });
    }
    static boolean daXn=false;
    private void sendOtp(String email) {
        OtpRequest request = new OtpRequest(email);

        RetrofitClient.getInstance()
                .getApi()
                .sendOTPreset(request)
                .enqueue(new Callback<OtpResponse>() {
                    @Override
                    public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            OtpResponse otpResponse = response.body();
                            if (otpResponse.isSuccess()) {
                                // Chuyển đến màn hình xác nhận OTP
                                Intent intent = new Intent(loginActivity.this, xacnhanOTP.class);
                                intent.putExtra("email", email);
                            } else {
                                Toast.makeText(loginActivity.this, otpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(loginActivity.this, xacnhanOTP.class);
                                intent.putExtra("email", email);
                                intent.putExtra("resetpass", true);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(loginActivity.this, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OtpResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi kết nối: ", t);
                    }
                });
        if(registerActivity.daXn){
            finish();
        }
    }
    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        return true;
    }

    private void loginUser(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        
        RetrofitClient.getInstance()
                .getApi()
                .login(loginRequest)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();
                            if (loginResponse.isSuccess()) {
                                // Lưu thông tin người dùng
                                saveUserData(loginResponse);
                                checkTokenExpiration();


                            } else {
                                Toast.makeText(loginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(loginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi kết nối: ", t);
                        Toast.makeText(loginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(LoginResponse response) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        LoginResponse.LoginData data = response.getData();
        if (data != null) {
            editor.putString(KEY_TOKEN, data.getToken());
            editor.putString(KEY_TOKEN_TYPE, data.getType());
            editor.putString(KEY_USER_EMAIL, data.getEmail());
            editor.putString(KEY_USER_AVATAR, data.getAvatar());
            editor.putInt(KEY_ACCOUNT_TYPE, data.getTypeOfAccount());
        }
        
        editor.apply();
    }
    private void checkTokenExpiration() {
        String token = sharedPreferences.getString(KEY_TOKEN, null);
        System.out.println("token: " +token);
        if (token != null) {
            RetrofitClient.getInstance()
                    .getApi()
                    .checkTokenExpiration("Bearer " + token) // Sửa nếu cần
                    .enqueue(new Callback<TokenCheckResponse>() {
                        @Override
                        public void onResponse(Call<TokenCheckResponse> call, Response<TokenCheckResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                TokenCheckResponse tokenCheckResponse = response.body();
                                Gson gson = new Gson();
                                System.out.println("tokenCheckResponse: "+ gson.toJson(tokenCheckResponse));
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putInt("nguoi_dung_id", tokenCheckResponse.getData().getIdUser()).apply();
                                System.out.println("nguoi_dung_id: "+sharedPreferences.getInt("nguoi_dung_id",0));
                                Toast.makeText(loginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                // Sửa tại đây
                                if (!"OK".equalsIgnoreCase(tokenCheckResponse.getStatus())) {
                                    Toast.makeText(loginActivity.this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<TokenCheckResponse> call, Throwable t) {
                            Log.e(TAG, "Lỗi kết nối: ", t);
                        }
                    });
        }
    }



}