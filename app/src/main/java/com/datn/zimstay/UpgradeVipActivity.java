package com.datn.zimstay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.datn.zimstay.R;
import com.datn.zimstay.api.ApiService;
import com.datn.zimstay.api.CreateOrder;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.TokenCheckResponse;
import com.google.gson.Gson;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class UpgradeVipActivity extends AppCompatActivity {
    private Button btnVip1, btnVip2, btnVip3;
    private ProgressDialog progressDialog;
    private String currentAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_vip);

        // Cho phép thực hiện network trên main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Khởi tạo ZaloPay SDK
        ZaloPaySDK.init(2553, Environment.SANDBOX);


        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnVip1 = findViewById(R.id.btnVip1);
        btnVip2 = findViewById(R.id.btnVip2);
        btnVip3 = findViewById(R.id.btnVip3);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
    }

    private void setupClickListeners() {
        btnVip1.setOnClickListener(v -> processPayment("99000"));
        btnVip2.setOnClickListener(v -> processPayment("199000"));
        btnVip3.setOnClickListener(v -> processPayment("299000"));
    }

    private void processPayment(String amount) {
        currentAmount = amount;
        progressDialog.show();
        new Thread(() -> {
            try {
                CreateOrder createOrder = new CreateOrder();
                JSONObject data = createOrder.createOrder(amount);
                
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    if (data != null) {
                        try {
                            int returnCode = data.getInt("return_code");
                            if (returnCode == 1) {
                                String token = data.getString("zp_trans_token");
                                // Mở ZaloPay để thanh toán
                                ZaloPaySDK.getInstance().payOrder(UpgradeVipActivity.this, token, "com.datn.zimstay://app", new PayOrderListener() {
                                    @Override
                                    public void onPaymentSucceeded(String transactionId, String transToken, String appTransId) {
                                        // Thanh toán thành công
                                        Toast.makeText(UpgradeVipActivity.this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                                        // Gọi API nâng cấp level
                                        upgradeUserLevel();
                                    }

                                    @Override
                                    public void onPaymentCanceled(String zpTransToken, String appTransId) {
                                        Toast.makeText(UpgradeVipActivity.this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransId) {
                                        Toast.makeText(UpgradeVipActivity.this, "Lỗi thanh toán: " + zaloPayError.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                String returnMessage = data.getString("return_message");
                                Toast.makeText(UpgradeVipActivity.this, "Lỗi: " + returnMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(UpgradeVipActivity.this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UpgradeVipActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(UpgradeVipActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void upgradeUserLevel() {
        progressDialog.setMessage("Đang nâng cấp tài khoản...");
        progressDialog.show();
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("nguoi_dung_id", 0);
        // Gọi API nâng cấp level
        RetrofitClient.getInstance().getApi().upgradeUserLevel(userId).enqueue(new Callback<TokenCheckResponse>() {
            @Override
            public void onResponse(Call<TokenCheckResponse> call, Response<TokenCheckResponse> response) {
                Gson gson = new Gson();
                String jsonResponse4 = gson.toJson(response.body());
                progressDialog.dismiss();
                System.out.println("rê: "+ gson.toJson(jsonResponse4));
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        TokenCheckResponse jsonResponse = response.body();
                        String status = jsonResponse.getStatus();
                        String message = jsonResponse.getMessage();
                        
                        if ("OK".equals(status)) {
                            Toast.makeText(UpgradeVipActivity.this, message, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(UpgradeVipActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(UpgradeVipActivity.this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpgradeVipActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenCheckResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(UpgradeVipActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
} 