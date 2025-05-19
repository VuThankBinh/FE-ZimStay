package com.datn.zimstay;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.datn.zimstay.api.ApiService;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.ChangePasswordRequest;
import com.datn.zimstay.api.models.TokenCheckResponse;
import com.datn.zimstay.api.models.UploadResponse;
import com.datn.zimstay.api.models.UpdateProfileRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class profileActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private TextView tvUserName;
    private TextView tvEmail;
    private EditText tvPhoneNumber;
    private EditText tvAddress;
    private EditText tvAge;
    private EditText tvIdCard;
    private TextView tvViolations;

    private TextView pass;
    private Button btnEdit;

    private BottomNavigationView bottomNavigationView;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    private AlertDialog currentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        bottomNavigationView = findViewById(R.id.navView);
        // Thiết lập item được chọn là Practice
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(profileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_message) {
                    Intent intent = new Intent(profileActivity.this, messageActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_notification) {
                    Intent intent = new Intent(profileActivity.this, notificationActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {

                    return true;
                }
                return false;
            }
        });
        initializeViews();
        setupImagePickers();
        getUser();

        // Xử lý sự kiện click vào avatar để thay đổi ảnh
        ivAvatar.setOnClickListener(v -> showImagePickerDialog());

        // Xử lý sự kiện click vào nút chỉnh sửa
        btnEdit.setOnClickListener(v -> updateProfile());

        // Xử lý sự kiện click vào mật khẩu để đổi mật khẩu
        pass.setOnClickListener(v -> showChangePasswordDialog());
    }
    
    private void initializeViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvAge = findViewById(R.id.tvAge);
        tvIdCard = findViewById(R.id.tvIdCard);
        tvViolations = findViewById(R.id.tvViolations);
        btnEdit = findViewById(R.id.btnEdit);
        pass = findViewById(R.id.tvPassword);
    }

    private void setupImagePickers() {
        // Xử lý chọn ảnh từ thư viện
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    uploadImage(selectedImageUri);
                }
            }
        );

        // Xử lý chụp ảnh từ camera
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    uploadImage(selectedImageUri);
                }
            }
        );
    }

    private void showImagePickerDialog() {
        String[] options = {"Chọn từ thư viện", "Chụp ảnh mới"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ảnh đại diện");
        
        // Tạo layout cho dialog với chiều cao cố định
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_image_picker, null);
        builder.setView(dialogView);

        // Tìm các button trong layout
        Button btnGallery = dialogView.findViewById(R.id.btnGallery);
        Button btnCamera = dialogView.findViewById(R.id.btnCamera);

        // Xử lý sự kiện click
        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
            if (currentDialog != null) {
                currentDialog.dismiss();
            }
        });

        btnCamera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(intent);
            if (currentDialog != null) {
                currentDialog.dismiss();
            }
        });

        currentDialog = builder.create();
        currentDialog.show();
    }

    private void uploadImage(Uri imageUri) {
        try {
            File imageFile = createTempFileFromUri(imageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);
Gson gson = new Gson();
            RetrofitClient.getInstance()
                .getApi()
                .uploadImage(filePart)
                .enqueue(new Callback<UploadResponse>() {
                    @Override
                    public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String imageUrl = response.body().getUrl();
                            updateProfileWithNewAvatar(imageUrl);
                        } else {
                            System.out.println("res is fail: " + response.isSuccessful());
                            System.out.println("res is fail: "+ requestFile.toString());
                            System.out.println("res is fail: " + response.message());
                            System.out.println("res is fail: " + gson.toJson(response.body()));
                            System.out.println("res is fail: " + response.code());
                            Toast.makeText(profileActivity.this, "Lỗi khi upload ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        Toast.makeText(profileActivity.this, "Lỗi kết nối khi upload ảnh", Toast.LENGTH_SHORT).show();
                    }
                });
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfileWithNewAvatar(String avatarUrl) {
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        
        if (token == null) {
            Toast.makeText(this, "Lỗi xác thực", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateProfileRequest request = new UpdateProfileRequest(
            tvUserName.getText().toString(),
            tvPhoneNumber.getText().toString(),
            tvAddress.getText().toString(),
            Integer.parseInt(tvAge.getText().toString()),
            avatarUrl,
            tvIdCard.getText().toString(),
            null // Không thay đổi mật khẩu
        );

        RetrofitClient.getInstance()
            .getApi()
            .updateProfile("Bearer " + token, request)
            .enqueue(new Callback<TokenCheckResponse>() {
                @Override
                public void onResponse(Call<TokenCheckResponse> call, Response<TokenCheckResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        updateUser(response.body().getData());
                        Toast.makeText(profileActivity.this, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(profileActivity.this, "Lỗi khi cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TokenCheckResponse> call, Throwable t) {
                    Toast.makeText(profileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void updateProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        
        if (token == null) {
            Toast.makeText(this, "Lỗi xác thực", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateProfileRequest request = new UpdateProfileRequest(
            tvUserName.getText().toString(),
            tvPhoneNumber.getText().toString(),
            tvAddress.getText().toString(),
            Integer.parseInt(tvAge.getText().toString()),
            null, // Giữ nguyên ảnh đại diện
            tvIdCard.getText().toString(),
            null // Không thay đổi mật khẩu
        );

        RetrofitClient.getInstance()
            .getApi()
            .updateProfile("Bearer " + token, request)
            .enqueue(new Callback<TokenCheckResponse>() {
                @Override
                public void onResponse(Call<TokenCheckResponse> call, Response<TokenCheckResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        updateUser(response.body().getData());
                        Toast.makeText(profileActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(profileActivity.this, "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TokenCheckResponse> call, Throwable t) {
                    Toast.makeText(profileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        
        TextInputEditText etCurrentPassword = dialogView.findViewById(R.id.etCurrentPassword);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        TextInputEditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);

        builder.setView(dialogView)
            .setTitle("Đổi mật khẩu")
            .setPositiveButton("Đổi mật khẩu", (dialog, which) -> {
                String currentPassword = etCurrentPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
                    Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                updatePassword(currentPassword, newPassword, confirmPassword);
            })
            .setNegativeButton("Hủy", null);

        builder.create().show();
    }

    private void updatePassword(String currentPassword, String newPassword, String confirmPassword) {
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        
        if (token == null) {
            Toast.makeText(this, "Lỗi xác thực", Toast.LENGTH_SHORT).show();
            return;
        }

        ChangePasswordRequest request = new ChangePasswordRequest(tvEmail.getText().toString(), currentPassword,newPassword, confirmPassword);
        Gson gson = new Gson();
        System.out.println(gson.toJson(request));
        RetrofitClient.getInstance()
            .getApi()
            .changePassword(request)
            .enqueue(new Callback<TokenCheckResponse>() {
                @Override
                public void onResponse(Call<TokenCheckResponse> call, Response<TokenCheckResponse> response) {
                    Gson gson = new Gson();
                    System.out.println(gson.toJson(response.body()));
                    System.out.println("res is success " + response.isSuccessful());
                    System.out.println("res is body " + gson.toJson(response.body()));
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(profileActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent(profileActivity.this, loginActivity.class);
                        startActivity(intent);
                        clearUser();
                        finish();
                    } else {
                        Toast.makeText(profileActivity.this, "Lỗi khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
                        System.out.println("lỗi: "+ response.message());
                    }
                }

                @Override
                public void onFailure(Call<TokenCheckResponse> call, Throwable t) {
                    Toast.makeText(profileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
    }
    private void clearUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    private File createTempFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("avatar", ".jpg", getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        
        inputStream.close();
        outputStream.close();
        
        return tempFile;
    }

    SharedPreferences sharedPreferences;
    private void getUser() {
        sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            RetrofitClient.getInstance()
                    .getApi()
                    .getUser("Bearer " + token)
                    .enqueue(new Callback<TokenCheckResponse>() {
                        @Override
                        public void onResponse(Call<TokenCheckResponse> call, Response<TokenCheckResponse> response) {
                            System.out.println("res is success" + response.isSuccessful());
                            System.out.println("res is body" + response.body());
                            if (response.isSuccessful() && response.body() != null) {
                                TokenCheckResponse tokenCheckResponse = response.body();
                                TokenCheckResponse.TokenData tokenData = tokenCheckResponse.getData();
                                // Gọi update UI hoặc truyền sang ViewModel
                                String jsonString = new Gson().toJson(tokenData);
                                System.out.println(jsonString);
                                updateUser(tokenData);
                            } else {
                                // Xử lý lỗi
                                Toast.makeText(profileActivity.this, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                                System.out.println("res is fail: " + response.message());
                            }
                        }
                        @Override
                        public void onFailure(Call<TokenCheckResponse> call, Throwable t) {
                            // Xử lý lỗi
                            System.out.println("res is fail: " + t.getMessage());
                        }
                    });
        }
    }

    private void updateUser(TokenCheckResponse.TokenData tokenData) {
        if (tokenData == null) return;

        tvUserName.setText(tokenData.getUserName());
        tvEmail.setText(tokenData.getEmail());
        tvPhoneNumber.setText(tokenData.getPhoneNumber());
        tvAddress.setText(tokenData.getAddress());
        tvAge.setText(String.valueOf(tokenData.getAge()));
        tvIdCard.setText(tokenData.getIdCardNumber());
        tvViolations.setText(String.valueOf(tokenData.getNumOfViolations()));
        Glide.with(this)
                .load(RetrofitClient.getInstance()
                        .getApi().getImage() +"/"+tokenData.getAvatar()) // URL ảnh
                .placeholder(R.drawable.user_1) // ảnh mặc định nếu chưa load
                .error(R.drawable.user_2) // ảnh khi load lỗi
                .circleCrop() // nếu muốn ảnh tròn
                .into(ivAvatar);
    }
}