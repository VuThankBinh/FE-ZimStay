package com.datn.zimstay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.Gson;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.OtpRequest;
import com.datn.zimstay.api.models.OtpResponse;
import com.datn.zimstay.api.models.RegisterRequest;
import com.datn.zimstay.api.models.RegisterResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class registerActivity extends AppCompatActivity {
    private static final String TAG = "registerActivity";
    private static final int REQUEST_OTP = 1;
    
    private EditText etEmail, etPassword, etConfirmPassword, etUserName;
    private EditText etPhoneNumber, etAddress, etAge, etIdCardNumber;
    private RadioGroup rgAccountType;
    private Button btnRegister;
    private TextView tvLogin;

    // Lưu trữ thông tin đăng ký tạm thời
    private String tempEmail, tempPassword, tempConfirmPassword, tempUserName;
    private String tempPhoneNumber, tempAddress, tempAgeStr, tempIdCardNumber;
    private int tempAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Ánh xạ các view
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etUserName = findViewById(R.id.etUserName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAddress = findViewById(R.id.etAddress);
        etAge = findViewById(R.id.etAge);
        etIdCardNumber = findViewById(R.id.etIdCardNumber);
        rgAccountType = findViewById(R.id.rgAccountType);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Xử lý sự kiện click nút đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                String userName = etUserName.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String ageStr = etAge.getText().toString().trim();
                String idCardNumber = etIdCardNumber.getText().toString().trim();
                
                // Lấy loại tài khoản
                int accountType = 0; // Mặc định là người dùng
                int selectedId = rgAccountType.getCheckedRadioButtonId();
                if (selectedId == R.id.rbHost) {
                    accountType = 1; // Chủ nhà
                }
                
                if (validateInput(email, password, confirmPassword, userName, phoneNumber, address, ageStr, idCardNumber)) {
                    // Lưu thông tin tạm thời
                    saveTempData(email, password, confirmPassword, userName, phoneNumber, address, ageStr, idCardNumber, accountType);
                    // Gửi OTP
                    sendOtp(email);
                }
            }
        });

        // Xử lý sự kiện click đăng nhập
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình đăng nhập
            }
        });
    }
    static boolean daXn=false;
    private void saveTempData(String email, String password, String confirmPassword, String userName,
                            String phoneNumber, String address, String ageStr, String idCardNumber, int accountType) {
        tempEmail = email;
        tempPassword = password;
        tempConfirmPassword = confirmPassword;
        tempUserName = userName;
        tempPhoneNumber = phoneNumber;
        tempAddress = address;
        tempAgeStr = ageStr;
        tempIdCardNumber = idCardNumber;
        tempAccountType = accountType;
    }

    private void sendOtp(String email) {
        OtpRequest request = new OtpRequest(email);
        
        RetrofitClient.getInstance()
                .getApi()
                .sendOtp(request)
                .enqueue(new Callback<OtpResponse>() {
                    @Override
                    public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            OtpResponse otpResponse = response.body();
                            if (otpResponse.isSuccess()) {
                                // Chuyển đến màn hình xác nhận OTP
                                Intent intent = new Intent(registerActivity.this, xacnhanOTP.class);
                                intent.putExtra("email", email);
                                startActivityForResult(intent, REQUEST_OTP);
                            } else {
                                Toast.makeText(registerActivity.this, otpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(registerActivity.this, xacnhanOTP.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(registerActivity.this, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OtpResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi kết nối: ", t);
                        Toast.makeText(registerActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        if(registerActivity.daXn){
            finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Luôn chạy lại khi quay về Activity này
        if(daXn){
            int age = Integer.parseInt(tempAgeStr);
            registerUser(tempEmail, tempPassword, tempConfirmPassword, tempUserName,
                    tempPhoneNumber, tempAddress, age, tempAccountType, tempIdCardNumber);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OTP && resultCode == Activity.RESULT_OK) {
            // OTP xác thực thành công, tiến hành đăng ký
            int age = Integer.parseInt(tempAgeStr);
            registerUser(tempEmail, tempPassword, tempConfirmPassword, tempUserName,
                    tempPhoneNumber, tempAddress, age, tempAccountType, tempIdCardNumber);
        }
    }

    private boolean validateInput(String email, String password, String confirmPassword, String userName,
                                String phoneNumber, String address, String ageStr, String idCardNumber) {
        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            return false;
        }
        if (userName.isEmpty()) {
            etUserName.setError("Vui lòng nhập họ tên");
            return false;
        }
        if (phoneNumber.isEmpty()) {
            etPhoneNumber.setError("Vui lòng nhập số điện thoại");
            return false;
        }
        if (address.isEmpty()) {
            etAddress.setError("Vui lòng nhập địa chỉ");
            return false;
        }
        if (ageStr.isEmpty()) {
            etAge.setError("Vui lòng nhập tuổi");
            return false;
        }
        if (idCardNumber.isEmpty()) {
            etIdCardNumber.setError("Vui lòng nhập số CMND/CCCD");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Vui lòng nhập lại mật khẩu");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu không khớp");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        return true;
    }

    private void registerUser(String email, String password, String confirmPassword, String userName,
                            String phoneNumber, String address, int age, int accountType, String idCardNumber) {
        RegisterRequest registerRequest = new RegisterRequest(email, password, confirmPassword, userName,
                phoneNumber, address, age, accountType, idCardNumber);
        
        RetrofitClient.getInstance()
                .getApi()
                .register(registerRequest)
                .enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RegisterResponse registerResponse = response.body();
                            
                            if (registerResponse.isSuccess()) {
                                Toast.makeText(registerActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                finish(); // Quay lại màn hình đăng nhập
                            } else {
                                Toast.makeText(registerActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Gson gson = new Gson();
                            String json = gson.toJson(registerRequest);
                            Log.d("RegisterRequestJSON", json);
                            System.out.println("JSON gửi đi: " + gson.toJson(registerRequest));
                            Toast.makeText(registerActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi kết nối: ", t);
                        Toast.makeText(registerActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}