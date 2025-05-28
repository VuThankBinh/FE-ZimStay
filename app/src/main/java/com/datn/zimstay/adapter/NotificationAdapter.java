package com.datn.zimstay.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.zimstay.R;
import com.datn.zimstay.api.RetrofitClient;
import com.datn.zimstay.api.models.NotificationResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private Context context;
    private List<NotificationResponse> notifications;
    private int currentUserId;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick();
    }

    public NotificationAdapter(Context context, List<NotificationResponse> notifications, int currentUserId) {
        this.context = context;
        this.notifications = notifications;
        this.currentUserId = currentUserId;
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationResponse notification = notifications.get(position);
        
        // Set title và message
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        
        // Format thời gian
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", new Locale("vi"));
            Date date = inputFormat.parse(notification.getCreatedAt());
            holder.tvTime.setText(outputFormat.format(date));
        } catch (ParseException e) {
            holder.tvTime.setText(notification.getCreatedAt());
        }

        // Set style dựa vào isRead
        if (notification.isRead()) {
            // Thông báo đã đọc - màu xám
            holder.tvTitle.setTextColor(Color.GRAY);
            holder.tvMessage.setTextColor(Color.GRAY);
            holder.tvTime.setTextColor(Color.GRAY);
            holder.cardView.setCardElevation(2f);
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        } else {
            // Thông báo chưa đọc - màu đen đậm và nổi lên
            holder.tvTitle.setTextColor(Color.BLACK);
            holder.tvMessage.setTextColor(Color.BLACK);
            holder.tvTime.setTextColor(Color.BLACK);
            holder.cardView.setCardElevation(8f);
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            showNotificationDetail(notification);
            if (!notification.isRead()) {
                markAsRead(notification.getId());
            }
        });
    }

    private void showNotificationDetail(NotificationResponse notification) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_notification_detail, null);
        
        TextView tvTitle = view.findViewById(R.id.tv_detail_title);
        TextView tvMessage = view.findViewById(R.id.tv_detail_message);
        TextView tvTime = view.findViewById(R.id.tv_detail_time);

        tvTitle.setText(notification.getTitle());
        tvMessage.setText(notification.getMessage());
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", new Locale("vi"));
            Date date = inputFormat.parse(notification.getCreatedAt());
            tvTime.setText(outputFormat.format(date));
        } catch (ParseException e) {
            tvTime.setText(notification.getCreatedAt());
        }

        builder.setView(view);
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void markAsRead(int notificationId) {
        RetrofitClient.getInstance().getApi().markNotificationAsRead(notificationId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            if (listener != null) {
                                listener.onNotificationClick();
                            }
                        } else {
                            Toast.makeText(context, "Không thể đánh dấu thông báo đã đọc", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        CardView cardView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_notification_title);
            tvMessage = itemView.findViewById(R.id.tv_notification_message);
            tvTime = itemView.findViewById(R.id.tv_notification_time);
            cardView = itemView.findViewById(R.id.card_notification);
        }
    }
}
