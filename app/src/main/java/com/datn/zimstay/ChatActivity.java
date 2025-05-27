package com.datn.zimstay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.zimstay.adapter.ApartmentAdapter;
import com.datn.zimstay.adapter.ApartmentAdapter2;
import com.datn.zimstay.adapter.ChatAdapter;
import com.datn.zimstay.adapter.ImageSliderAdapter;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.api.ApiService;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.Amenity;
import com.datn.zimstay.api.models.ApartmentData;
import com.datn.zimstay.api.models.ApartmentResponse;
import com.datn.zimstay.api.models.ImageItem;
import com.datn.zimstay.api.models.MessageRequest;
import com.datn.zimstay.api.models.MessageResponse;
import com.datn.zimstay.utils.EncryptionUtils;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerChat;
    private EditText edtMessage;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private List<MessageResponse> messageList = new ArrayList<>();
    private int conversationId;
    private int currentUserId;
    private int otherUserId;
    private String otherUserName;

    private int[] apartmentsId;

    private FirebaseFirestore db;
    private ListenerRegistration messageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Lấy thông tin từ Intent
        conversationId = getIntent().getIntExtra("conversation_id", -1);
        otherUserId = getIntent().getIntExtra("other_user_id", -1);
        otherUserName = getIntent().getStringExtra("userName");
        String avatar = getIntent().getStringExtra("avatar");
        apartmentsId = getIntent().getIntArrayExtra("apartmentIds");
        System.out.println("apartmentIds: " + apartmentsId.length);
        setupRecyclerView();
        getRooms(apartmentsId);

        System.out.println("avatar: " + avatar);

        ImageView img_user_avatar = findViewById(R.id.img_user_avatar);
        Glide.with(img_user_avatar.getContext())
                .load(avatar)
                .placeholder(R.drawable.user_1)
                .into(img_user_avatar);

        TextView text_user_name = findViewById(R.id.text_user_name);
        text_user_name.setText(otherUserName);

        ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Lấy ID người dùng hiện tại từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ZimStayPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("nguoi_dung_id", 0);

        // Khởi tạo views
        recyclerChat = findViewById(R.id.recycler_chat);
        edtMessage = findViewById(R.id.edt_message);
        btnSend = findViewById(R.id.btn_send);

        // Thiết lập RecyclerView
        chatAdapter = new ChatAdapter(messageList, currentUserId);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(chatAdapter);

        // Load tin nhắn
        loadMessages();

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Thiết lập listener cho tin nhắn mới
        setupMessageListener();

        // Xử lý sự kiện gửi tin nhắn
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    edtMessage.setText("");
                }
            }
        });
    }
    private List<ApartmentData> apartmentDataList = new ArrayList<>();
    private ApartmentAdapter2 apartmentAdapter;

    private void getRooms(int[] apartmentsId) {
        apartmentDataList.clear();

        ApiService apiService = RetrofitClient.getInstance().getApi();

        for (int id : apartmentsId) {
            Call<ApartmentResponse> call = apiService.getApartmentById(id);
            call.enqueue(new Callback<ApartmentResponse>() {
                @Override
                public void onResponse(Call<ApartmentResponse> call, Response<ApartmentResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApartmentData data = response.body().getData();
                        apartmentDataList.add(data);
                        apartmentAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ChatActivity.this, "Lỗi khi lấy dữ liệu cho id: " + id, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApartmentResponse> call, Throwable t) {
                    Toast.makeText(ChatActivity.this, "Không thể kết nối tới server cho id: " + id, Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        }
    }

    // Trong onCreate() hoặc nơi nào đó bạn setup RecyclerView:
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_rooms);
        apartmentAdapter = new ApartmentAdapter2(this, apartmentDataList, new ApartmentAdapter2.OnApartmentClickListener2() {
            @Override
            public void onApartmentClick2(int apartmentId) {
                Intent intent = new Intent(ChatActivity.this, roomDetailActivity.class);
                intent.putExtra("apartment_id", apartmentId);
                startActivity(intent);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(apartmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadMessages() {
        Call<List<MessageResponse>> call = RetrofitClient.getInstance().getApi().getMessagesByConversationId(conversationId, currentUserId);
        System.out.println(call.request().url());
        RetrofitClient.getInstance().getApi().getMessagesByConversationId(conversationId, currentUserId)
                .enqueue(new Callback<List<MessageResponse>>() {
                    @Override
                    public void onResponse(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            messageList.clear();
                            messageList.addAll(response.body());
                            chatAdapter.notifyDataSetChanged();
                            recyclerChat.scrollToPosition(messageList.size() - 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                        Toast.makeText(ChatActivity.this, "Lỗi khi tải tin nhắn", Toast.LENGTH_SHORT).show();
                        System.out.println(t);
                    }
                });
    }

    private void setupMessageListener() {
        // Lắng nghe thay đổi của messages trong conversation cụ thể
        messageListener = db.collection("conversations")
            .document(String.valueOf(conversationId))
            .collection("messages")
            .addSnapshotListener((snapshots, error) -> {
                if (error != null) {
                    return;
                }
                
                if (snapshots != null) {
                    // Có thay đổi trong messages, load lại tin nhắn
                    loadMessages();
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy listener khi activity bị hủy
        if (messageListener != null) {
            messageListener.remove();
        }
    }

    private void sendMessage(String message) {
        // Mã hóa tin nhắn trước khi gửi
        String encryptedMessage = EncryptionUtils.encrypt(message);
        
        MessageRequest messageRequest = new MessageRequest(
            conversationId,
            currentUserId,
            otherUserId,
            encryptedMessage
        );

        // Gọi API gửi tin nhắn
        RetrofitClient.getInstance().getApi().sendMessage(messageRequest)
            .enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        MessageResponse messageResponse = response.body();
                        
                        // Lưu vào Firestore
                        Map<String, Object> messageData = new HashMap<>();
                        messageData.put("message", messageResponse.getMessage());
                        messageData.put("senderId", messageResponse.getSenderId());
                        messageData.put("receiverId", otherUserId);
                        messageData.put("createdAt", FieldValue.serverTimestamp());
                        
                        // Lưu message
                        db.collection("conversations")
                            .document(String.valueOf(conversationId))
                            .collection("messages")
                            .document(String.valueOf(System.currentTimeMillis()))
                            .set(messageData);
                            
                        messageList.add(messageResponse);
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerChat.scrollToPosition(messageList.size() - 1);
                    } else {
                        Toast.makeText(ChatActivity.this, "Lỗi khi gửi tin nhắn", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    Toast.makeText(ChatActivity.this, "Lỗi khi gửi tin nhắn", Toast.LENGTH_SHORT).show();
                }
            });
    }
}