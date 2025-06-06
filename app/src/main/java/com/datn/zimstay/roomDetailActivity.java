package com.datn.zimstay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.api.ApiService;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.Amenity;
import com.datn.zimstay.api.models.ApartmentData;
import com.datn.zimstay.api.models.ApartmentResponse;
import com.datn.zimstay.api.models.ConversationResponse;
import com.datn.zimstay.api.models.ImageItem;
import com.datn.zimstay.adapter.ImageSliderAdapter;
import com.datn.zimstay.api.models.TokenCheckResponse;
import com.google.gson.Gson;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKBReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class roomDetailActivity extends AppCompatActivity {

    Integer id=9;
    Integer ownerId=0;
    private static final String TAG = "loginActivity";
    private static final String PREF_NAME = "ZimStayPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_AVATAR = "user_avatar";
    private static final String KEY_ACCOUNT_TYPE = "account_type";
    private Integer userId;

    private ViewPager2 viewPager;
    private TextView tvStatus, tvAddress, tvCost, tvArea, tvDeposit, tvCity, tvDetailInfo;
    private LinearLayout layoutAmenities;
    private ImageView back;
    SharedPreferences sharedPreferences;
    private LinearLayout goMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_detail);
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        userId=sharedPreferences.getInt("nguoi_dung_id",0);
        initializeViews();
        Intent intent = getIntent();
        id = intent.getIntExtra("apartment_id", -1);
        goMap=findViewById(R.id.goMap);
        goMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocationOnMap();
            }
        });
        fetchRoomDetail();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button btnChat= findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createConversation();
            }
        });
        Button btnDatLich=findViewById(R.id.btnBook);
        btnDatLich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(roomDetailActivity.this, DatLichActivity.class);
                intent.putExtra("apartment_id", id);
                intent.putExtra("ownerId", ownerId);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });


    }
    private void createConversation() {
        System.out.println("id: " + id);
        System.out.println("userId: " + userId);
        System.out.println("ownerId: " + ownerId);

        RetrofitClient
                .getInstance()
                .getApi()
                .createConversation(userId, ownerId, id)
                .enqueue(new Callback<ConversationResponse>() {
                    @Override
                    public void onResponse(Call<ConversationResponse> call, Response<ConversationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ConversationResponse conversationResponse = response.body();
                            int conversationId = conversationResponse.getId();
                            Intent intent = new Intent(roomDetailActivity.this, ChatActivity.class);
                            intent.putExtra("apartment_id", id);
                            intent.putExtra("ownerId", userId);
                            intent.putExtra("conversation_id", conversationId);
                            intent.putExtra("other_user_id", ownerId);
                            List<Integer> ids = conversationResponse.getApartmentIds();
                            int[] idArray = new int[ids.size()];
                            for (int i = 0; i < ids.size(); i++) {
                                idArray[i] = ids.get(i);
                            }
                            intent.putExtra("apartmentIds", idArray);
                            String[] userName=new String[1];
                            String[] imageUrl=new String[1];
                            RetrofitClient.getInstance().getApi().getUserById(ownerId)
                                    .enqueue(new Callback<TokenCheckResponse>() {
                                        @Override
                                        public void onResponse(Call<TokenCheckResponse> call, Response<TokenCheckResponse> response) {

                                            Gson gson = new Gson();
                                            System.out.println("userResponse: "+gson.toJson(response.body()));
                                            if (response.isSuccessful() && response.body() != null) {
                                                TokenCheckResponse userResponse = response.body();
                                                // Hiển thị tên người dùng
                                               userName[0]= userResponse.getData().getUserName();
                                                imageUrl[0]=ApiConfig.Base_url+"uploads/images/"+userResponse.getData().getAvatar();
                                                System.out.println("userName: "+userName[0]);
                                                System.out.println("imageUrl: "+imageUrl[0]);

                                                intent.putExtra("userName", userName[0]);
                                                intent.putExtra("avatar", imageUrl[0]);
                                                startActivity(intent);

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<TokenCheckResponse> call, Throwable t) {
                                            t.printStackTrace();
                                        }
                                    });

                        } else {
                            Toast.makeText(roomDetailActivity.this, "Lỗi khi tạo cuộc hội thoại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ConversationResponse> call, Throwable t) {
                        Toast.makeText(roomDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        tvStatus = findViewById(R.id.tvStatus);
        tvAddress = findViewById(R.id.tvAddress);
        tvCost = findViewById(R.id.tvCost);
        tvArea = findViewById(R.id.tvArea);
        tvDeposit = findViewById(R.id.tvDeposit);
        tvCity = findViewById(R.id.tvCity);
        tvDetailInfo = findViewById(R.id.tvDetailInfo);
        layoutAmenities = findViewById(R.id.layoutAmenities);
        back = findViewById(R.id.back);
    }
    private String locationHex="";
    private String diaChi="";
    public static double[] decodeLocation(String hex) {
        try {
            byte[] wkb = hexStringToByteArray(hex);
            WKBReader reader = new WKBReader();
            Geometry geometry = reader.read(wkb);

            double latitude = geometry.getCoordinate().y;
            double longitude = geometry.getCoordinate().x;
            return new double[]{latitude, longitude};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)
                    ((Character.digit(s.charAt(i), 16) << 4)
                            + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    public static double[] getLatLngFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new double[]{location.getLatitude(), location.getLongitude()};
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // hoặc trả mảng rỗng nếu thất bại
    }
    private void openLocationOnMap(){
        try {
            // Chuyển hex string thành mảng byte
            double[] bytes = decodeLocation(locationHex);
            double[] getbyAd= getLatLngFromAddress(roomDetailActivity.this, diaChi);
            System.out.println("locationHex: "+ locationHex);


            // Bỏ qua 5 byte đầu (SRID + endianness), giải mã longitude và latitude
           double lon = getbyAd[1]; // longitude
            double lat = getbyAd[0]; // latitude
            System.out.println("lon: "+lon);
            System.out.println("lan: "+lat);

            // Tạo intent mở Google Maps
            Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(roomDetailActivity.this.getPackageManager()) != null) {
                roomDetailActivity.this.startActivity(mapIntent);

            } else {
                // Nếu không có Google Maps, dùng browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://maps.google.com/?q=" + lat + "," + lon));
                System.out.println("map uri: "+ "https://maps.google.com/?q=" + lat + "," + lon);
                roomDetailActivity.this.startActivity(browserIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý lỗi nếu không decode được
        }
    }
    private void fetchRoomDetail() {
        ApiService apiService = RetrofitClient.getInstance().getApi();
        Call<ApartmentResponse> call = apiService.getApartmentById(id);
        call.enqueue(new Callback<ApartmentResponse>() {
            @Override
            public void onResponse(Call<ApartmentResponse> call, Response<ApartmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApartmentResponse apartment = response.body();
                    ApartmentData data = apartment.getData();
                    Gson gson=new Gson();
                    System.out.println("data: "+gson.toJson(data));
                    ownerId= data.getOwnerId();
                    locationHex=data.getLocation();
                    // Cập nhật UI với dữ liệu nhận được
                    tvStatus.setText(data.getStatus());
                    tvAddress.setText(data.getAddress());
                    tvCity.setText(data.getCity());
                    tvArea.setText(data.getArea() + "m²");
                    tvDeposit.setText(String.format("%,dđ", data.getHouseDeposit()).replace(",", "."));
                    tvCost.setText(String.format("%,dđ/tháng", data.getCost()).replace(",", "."));
//                    tvDetailInfo.setText(data.getDetailInfo());
                    diaChi=data.getAddress() +", "+ data.getWard() +", "+data.getDistrict()+", "+data.getCity();
                    // Load ảnh vào ViewPager2
                    List<ImageItem> imageItems = data.getImages();
                    String[] imageUrls = new String[imageItems.size()];
                    for (int i = 0; i < imageItems.size(); i++) {
                        imageUrls[i] = ApiConfig.Base_url+ "uploads/images/" +imageItems.get(i).getImageUrl();
                    }
                    ImageSliderAdapter adapter = new ImageSliderAdapter(imageUrls);
                    viewPager.setAdapter(adapter);

                    // Hiển thị tiện ích
                    layoutAmenities.removeAllViews();
                    for (Amenity amenity : data.getAmenities()) {
                        TextView tvAmenity = new TextView(roomDetailActivity.this);
                        tvAmenity.setText(amenity.getAmenityName() + ": " + String.format("%,dđ", amenity.getPricePerUnit()).replace(",", ".")+"/"+ amenity.getAmenityUnit());
                        tvAmenity.setTextSize(14);
                        tvAmenity.setPadding(16, 8, 16, 8);
                        tvAmenity.setBackgroundResource(android.R.drawable.editbox_background);
                        layoutAmenities.addView(tvAmenity);
                    }

                } else {
                    // Xử lý khi response lỗi hoặc body null
                    Toast.makeText(roomDetailActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApartmentResponse> call, Throwable t) {
                // Xử lý lỗi mạng hoặc lỗi khác
                Toast.makeText(roomDetailActivity.this, "Không thể kết nối tới server", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}