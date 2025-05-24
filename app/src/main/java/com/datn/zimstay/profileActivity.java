package com.datn.zimstay;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.datn.zimstay.api.ApiConfig;
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
import okhttp3.ResponseBody;
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

    private static final int PERMISSION_REQUEST_CODE = 100;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    private Runnable pendingAction;

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
        Button logout=findViewById(R.id.btnLogOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUser();
                Intent intent =new Intent(profileActivity.this, loginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Khởi tạo permission launcher
        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    if (!isGranted) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    // Nếu được cấp quyền, tiếp tục với hành động đã chọn
                    if (pendingAction != null) {
                        pendingAction.run();
                        pendingAction = null;
                    }
                } else {
                    // Nếu bị từ chối, hiển thị thông báo và hướng dẫn vào cài đặt
                    new AlertDialog.Builder(this)
                        .setTitle("Cần cấp quyền")
                        .setMessage("Để sử dụng tính năng này, vui lòng cấp quyền trong Cài đặt")
                        .setPositiveButton("Vào cài đặt", (dialog, which) -> {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
                }
            }
        );

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
                    if (selectedImageUri != null) {
                        uploadImage(selectedImageUri);
                    }
                }
            }
        );

        // Xử lý chụp ảnh từ camera
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null && extras.containsKey("data")) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        // Lưu bitmap vào file tạm
                        try {
                            File tempFile = createTempFileFromBitmap(imageBitmap);
                            selectedImageUri = Uri.fromFile(tempFile);
                            uploadImage(selectedImageUri);
                        } catch (IOException e) {
                            Toast.makeText(this, "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        );
    }

    private File createTempFileFromBitmap(Bitmap bitmap) throws IOException {
        File tempFile = File.createTempFile("avatar", ".jpg", getCacheDir());
        FileOutputStream fos = new FileOutputStream(tempFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.close();
        return tempFile;
    }

    private void checkAndRequestPermissions(Runnable action) {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) 
                != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            action.run();
        } else {
            // Kiểm tra xem có nên hiển thị giải thích không
            boolean shouldShowRationale = false;
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    shouldShowRationale = true;
                    break;
                }
            }

            if (shouldShowRationale) {
                // Hiển thị dialog giải thích
                new AlertDialog.Builder(this)
                    .setTitle("Cần cấp quyền")
                    .setMessage("Ứng dụng cần quyền truy cập camera và thư viện ảnh để thay đổi ảnh đại diện")
                    .setPositiveButton("OK", (dialog, which) -> {
                        pendingAction = action;
                        requestPermissionLauncher.launch(permissions);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            } else {
                // Xin quyền trực tiếp
                pendingAction = action;
                requestPermissionLauncher.launch(permissions);
            }
        }
    }

    private void showImagePickerDialog() {
        String[] options = {"Chọn từ thư viện", "Chụp ảnh mới"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ảnh đại diện");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Chọn từ thư viện
                checkAndRequestPermissions(() -> {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    imagePickerLauncher.launch(intent);
                });
            } else {
                // Chụp ảnh mới
                checkAndRequestPermissions(() -> {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        cameraLauncher.launch(intent);
                    } else {
                        Toast.makeText(this, "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.show();
    }

    private void uploadImage(Uri imageUri) {
        try {
            // Hiển thị ảnh đã chọn ngay lập tức
            Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.user_1)
                .error(R.drawable.user_2)
                .circleCrop()
                .into(ivAvatar);

            File imageFile = createTempFileFromUri(imageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "avatar_" + System.currentTimeMillis() + ".jpg", requestFile);

            RetrofitClient.getInstance()
                .getApi()
                .uploadImage(filePart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String responseString = response.body().string();
                                // Tách đường dẫn từ chuỗi trả về
                                // Ví dụ: "File đã được lưu tại: D:\\...\\uploads\\images\\ic_warning.png"
                                String[] parts = responseString.split("tại:");
                                if (parts.length > 1) {
                                    String fullPath = parts[1].trim();
                                    String fileName = fullPath.substring(fullPath.lastIndexOf("\\") + 1);
                                    
                                    // Tạo URL đầy đủ cho ảnh
                                    String imageUrl =   ApiConfig.Base_url +"uploads/images/" + fileName;
                                    
                                    // Load ảnh từ URL
                                    Glide.with(profileActivity.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.user_1)
                                        .error(R.drawable.user_2)
                                        .circleCrop()
                                        .into(ivAvatar);

                                    updateProfileWithNewAvatar(fileName);
                                } else {
                                    Toast.makeText(profileActivity.this, "Không tìm thấy đường dẫn file", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                Toast.makeText(profileActivity.this, "Lỗi khi đọc phản hồi từ server", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(profileActivity.this, "Upload ảnh thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(profileActivity.this, "Lỗi kết nối khi upload ảnh", Toast.LENGTH_SHORT).show();
                    }
                });
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfileWithNewAvatar(String avatarUrl) {
        System.out.println("lỗi: đã chạy ham r");
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        System.out.println("avatar: "+ avatarUrl);
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
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            System.out.println("Update profile failed: " + errorBody);
                            Toast.makeText(profileActivity.this, "Lỗi khi cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(profileActivity.this, "Lỗi khi cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TokenCheckResponse> call, Throwable t) {
                    System.out.println("Update profile failed: " + t.getMessage());
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
        
        // Tạo URL đầy đủ cho ảnh
        String imageUrl =  ApiConfig.Base_url+ "uploads/images/" + tokenData.getAvatar();
        
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.user_1)
            .error(R.drawable.user_2)
            .circleCrop()
            .into(ivAvatar);
    }
}