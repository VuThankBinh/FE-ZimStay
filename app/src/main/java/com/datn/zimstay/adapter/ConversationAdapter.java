package com.datn.zimstay.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.zimstay.R;
import com.datn.zimstay.api.ApiConfig;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.ConversationResponse;
import com.datn.zimstay.api.models.TokenCheckResponse;
import com.datn.zimstay.ChatActivity;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private List<ConversationResponse> conversations;
    private int currentUserId;
    private Context context;

    public ConversationAdapter(Context context, List<ConversationResponse> conversations, int currentUserId) {
        this.context = context;
        this.conversations = conversations;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    String apartmentsId;
    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        String avatar2[]={""};
        ConversationResponse conversation = conversations.get(position);
        
        // Xác định ID của người dùng khác trong cuộc hội thoại
        int otherUserId = (conversation.getUser1Id() == currentUserId) ?
                         conversation.getUser2Id() : conversation.getUser1Id();
        System.out.println("otherUserId: "+otherUserId);
        // Gọi API để lấy thông tin người dùng khác
        Call<TokenCheckResponse> call = RetrofitClient.getInstance().getApi().getUserById(otherUserId);
        System.out.println(call.request().url());
        RetrofitClient.getInstance().getApi().getUserById(otherUserId)
            .enqueue(new Callback<TokenCheckResponse>() {
                @Override
                public void onResponse(Call<TokenCheckResponse> call, Response<TokenCheckResponse> response) {

                    Gson gson = new Gson();
                    System.out.println("userResponse: "+gson.toJson(response.body()));
                    if (response.isSuccessful() && response.body() != null) {
                        TokenCheckResponse userResponse = response.body();
                            // Hiển thị tên người dùng
                            holder.textUsername.setText(userResponse.getData().getUserName());
                            avatar2[0]=ApiConfig.Base_url+"uploads/images/"+userResponse.getData().getAvatar();
                            // Hiển thị avatar người dùng
                            Glide.with(holder.imgAvatar.getContext())
                                .load(ApiConfig.Base_url+"uploads/images/"+userResponse.getData().getAvatar())
                                .placeholder(R.drawable.user_1)
                                .into(holder.imgAvatar);

                    }
                }

                @Override
                public void onFailure(Call<TokenCheckResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        // Thêm sự kiện click cho item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lưu thông tin cuộc hội thoại vào SharedPreferences
                SharedPreferences sharedPreferences = context.getSharedPreferences("chat_info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("conversation_id", conversation.getId());
                editor.putInt("other_user_id", otherUserId);
                editor.apply();

                // Chuyển đến màn hình chat
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("conversation_id", conversation.getId());
                intent.putExtra("other_user_id", otherUserId);
                intent.putExtra("userName", holder.textUsername.getText().toString());
                intent.putExtra("avatar", avatar2[0]);
                List<Integer> ids = conversation.getApartmentIds();
                int[] idArray = new int[ids.size()];
                for (int i = 0; i < ids.size(); i++) {
                    idArray[i] = ids.get(i);
                }
                System.out.println(apartmentsId);
                intent.putExtra("apartmentIds", idArray);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void setConversations(List<ConversationResponse> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView textUsername;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar_sent);
            textUsername = itemView.findViewById(R.id.text_username);
        }
    }
}
