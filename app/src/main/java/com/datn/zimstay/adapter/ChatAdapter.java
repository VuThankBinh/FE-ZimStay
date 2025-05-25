package com.datn.zimstay.adapter;

import android.icu.text.SimpleDateFormat;
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
import com.datn.zimstay.api.models.MessageResponse;
import com.datn.zimstay.utils.EncryptionUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private List<MessageResponse> messages;
    private int currentUserId;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<MessageResponse> messages, int currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        MessageResponse message = messages.get(position);
        if (message.getSenderId() == currentUserId) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageResponse message = messages.get(position);
        // Giải mã tin nhắn trước khi hiển thị
        String decryptedMessage = EncryptionUtils.decrypt(message.getMessage());
        
        // Hiển thị tin nhắn
        if (message.getSenderId() == currentUserId) {
            holder.tvMessageSent.setText(decryptedMessage);
            // Hiển thị avatar người gửi
            if (message.getSenderAvatar() != null) {
                Glide.with(holder.imgAvatarSent.getContext())
                    .load(ApiConfig.Base_url + "uploads/images/" + message.getSenderAvatar())
                    .placeholder(R.drawable.user_1)
                    .into(holder.imgAvatarSent);
            }

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

                Date date = inputFormat.parse(message.getCreatedAt());
                String formattedDate = outputFormat.format(date);

                holder.dateSent.setText(formattedDate);
            } catch (ParseException e) {
                holder.dateSent.setText("");
                e.printStackTrace();
            }

        } else {
            holder.tvMessageReceived.setText(decryptedMessage);
            // Hiển thị avatar người nhận
            if (message.getSenderAvatar() != null) {
                Glide.with(holder.imgAvatarReceived.getContext())
                    .load(ApiConfig.Base_url + "uploads/images/" + message.getSenderAvatar())
                    .placeholder(R.drawable.user_1)
                    .into(holder.imgAvatarReceived);
            }
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

                Date date = inputFormat.parse(message.getCreatedAt());
                String formattedDate = outputFormat.format(date);

                holder.dateReceived.setText(formattedDate);
            } catch (ParseException e) {
                holder.dateReceived.setText("");
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<MessageResponse> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addMessage(MessageResponse message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        // Views cho tin nhắn gửi
        TextView tvMessageSent;
        ImageView imgAvatarSent;
        TextView dateSent;
        
        // Views cho tin nhắn nhận
        TextView tvMessageReceived;
        ImageView imgAvatarReceived;
        TextView dateReceived;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            // Khởi tạo views cho tin nhắn gửi
            tvMessageSent = itemView.findViewById(R.id.tv_message_sent);
            imgAvatarSent = itemView.findViewById(R.id.img_avatar_sent);
            dateSent = itemView.findViewById(R.id.dateSent);
            
            // Khởi tạo views cho tin nhắn nhận
            tvMessageReceived = itemView.findViewById(R.id.tv_message_received);
            imgAvatarReceived = itemView.findViewById(R.id.img_avatar_sent);
            dateReceived = itemView.findViewById(R.id.dateReceived);
        }
    }
} 