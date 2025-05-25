package com.datn.zimstay;

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
import com.datn.zimstay.adapter.ChatAdapter;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.MessageRequest;
import com.datn.zimstay.api.models.MessageResponse;
import com.datn.zimstay.utils.EncryptionUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

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

    private void loadMessages() {
        Call<List<MessageResponse>> call = RetrofitClient.getInstance().getApi().getMessagesByConversationId(conversationId, currentUserId);
        System.out.println(call.request().url());
        RetrofitClient.getInstance().getApi().getMessagesByConversationId(conversationId, currentUserId)
                .enqueue(new Callback<List<MessageResponse>>() {
                    @Override
                    public void onResponse(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                        Gson gson = new GsonBuilder()
                                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS")
                                .create();
                        System.out.println("messageResponse: " + gson.toJson(response.body()));
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

    private void sendMessage(String message) {
        // Mã hóa tin nhắn trước khi gửi
        String encryptedMessage = EncryptionUtils.encrypt(message);
        
        MessageRequest messageRequest = new MessageRequest(
            conversationId,
            currentUserId,
            otherUserId,
            encryptedMessage
        );
        Call<MessageResponse> call = RetrofitClient.getInstance().getApi().sendMessage(messageRequest);
        System.out.println(call.request().url());
        Gson gson = new Gson();
        System.out.println(gson.toJson(messageRequest));
        RetrofitClient.getInstance().getApi().sendMessage(messageRequest)
            .enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        messageList.add(response.body());
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerChat.scrollToPosition(messageList.size() - 1);
                    } else {
                        Toast.makeText(ChatActivity.this, "Lỗi khi gửi tin nhắn", Toast.LENGTH_SHORT).show();
                        Gson gson = new Gson();
                        System.out.println(gson.toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    Toast.makeText(ChatActivity.this, "Lỗi khi gửi tin nhắn", Toast.LENGTH_SHORT).show();
                    System.out.println("error: "+ t);
                }
            });
    }
}