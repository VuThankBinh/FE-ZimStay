package com.datn.zimstay;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.OtpRequest;
import com.datn.zimstay.api.models.OtpResponse;
import com.datn.zimstay.api.models.VerifyOtpRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class xacnhanOTP extends AppCompatActivity {
    private static final String TAG = "xacnhanOTP";
    private EditText[] otpEditTexts;
    private Button btnConfirm;
    private TextView tvEmail, tvSendOtp;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xacnhan_otp);
        
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Lấy email từ Intent
        email = getIntent().getStringExtra("email");
        boolean resetPass = getIntent().getBooleanExtra("resetpass", false);

        if (email == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các view
        otpEditTexts = new EditText[]{
            findViewById(R.id.otpDigit1),
            findViewById(R.id.otpDigit2),
            findViewById(R.id.otpDigit3),
            findViewById(R.id.otpDigit4),
            findViewById(R.id.otpDigit5),
            findViewById(R.id.otpDigit6)
        };
        btnConfirm = findViewById(R.id.btnLogin);
        tvEmail = findViewById(R.id.etEmail);
        tvSendOtp = findViewById(R.id.sendOTP);

        // Hiển thị email
        tvEmail.setText(email);

        // Thiết lập TextWatcher cho các EditText OTP
        setupOtpEditTexts();

        // Xử lý sự kiện click nút xác nhận
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = getOtpCode();
                if (otp.length() == 6) {
                    if(resetPass){
                        verifyOtp2(email,otp);
                        return;
                    }
                    verifyOtp(email, otp);
                } else {
                    Toast.makeText(xacnhanOTP.this, "Vui lòng nhập đủ 6 ký tự OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý sự kiện click gửi lại OTP
        tvSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtp(email);
            }
        });
    }

    private void setupOtpEditTexts() {
        for (int i = 0; i < otpEditTexts.length; i++) {
            final int currentIndex = i;
            otpEditTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < otpEditTexts.length - 1) {
                        otpEditTexts[currentIndex + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private String getOtpCode() {
        StringBuilder otp = new StringBuilder();
        for (EditText editText : otpEditTexts) {
            otp.append(editText.getText().toString());
        }
        return otp.toString();
    }
    private void verifyOtp2(String email, String otp) {
        VerifyOtpRequest request = new VerifyOtpRequest(email, otp);

        RetrofitClient.getInstance()
                .getApi()
                .verifyOtp(request)
                .enqueue(new Callback<OtpResponse>() {
                    @Override
                    public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            OtpResponse otpResponse = response.body();
                            if (otpResponse.isSuccess()) {
                                Toast.makeText(xacnhanOTP.this, "Xác thực OTP thành công", Toast.LENGTH_SHORT).show();
                                // Trả về kết quả thành công cho màn hình đăng ký
                                Intent resultIntent = new Intent(xacnhanOTP.this, resetPassActivity.class);
                                resultIntent.putExtra("email", email);
                                setResult(RESULT_OK, resultIntent);
                                startActivity(resultIntent);
                                finish();

                            } else {
                                Toast.makeText(xacnhanOTP.this, otpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent(xacnhanOTP.this, resetPassActivity.class);
                                resultIntent.putExtra("email", email);
                                setResult(RESULT_OK, resultIntent);
                                startActivity(resultIntent);
                                finish();
                            }
                        } else {
                            Toast.makeText(xacnhanOTP.this, "Xác thực OTP thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OtpResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi kết nối: ", t);
                        Toast.makeText(xacnhanOTP.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyOtp(String email, String otp) {
        VerifyOtpRequest request = new VerifyOtpRequest(email, otp);
        
        RetrofitClient.getInstance()
                .getApi()
                .verifyOtp(request)
                .enqueue(new Callback<OtpResponse>() {
                    @Override
                    public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            OtpResponse otpResponse = response.body();
                            if (otpResponse.isSuccess()) {
                                Toast.makeText(xacnhanOTP.this, "Xác thực OTP thành công", Toast.LENGTH_SHORT).show();
                                // Trả về kết quả thành công cho màn hình đăng ký
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("email", email);
                                setResult(RESULT_OK, resultIntent);
                                registerActivity.daXn=true;
                                finish();

                            } else {
                                Toast.makeText(xacnhanOTP.this, otpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("email", email);
                                setResult(RESULT_OK, resultIntent);
                                registerActivity.daXn=true;
                                finish();
                            }
                        } else {
                            Toast.makeText(xacnhanOTP.this, "Xác thực OTP thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OtpResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi kết nối: ", t);
                        Toast.makeText(xacnhanOTP.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                                Toast.makeText(xacnhanOTP.this, "Đã gửi lại mã OTP", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(xacnhanOTP.this, otpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(xacnhanOTP.this, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OtpResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi kết nối: ", t);
                        Toast.makeText(xacnhanOTP.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}