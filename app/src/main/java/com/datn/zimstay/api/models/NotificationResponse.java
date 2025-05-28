package com.datn.zimstay.api.models;

public class NotificationResponse {
    private int id;
    private int userId;
    private String message;
    private String createdAt;
    private String title;
    private boolean isRead;

    public NotificationResponse(int id, int userId, String message, String createdAt, String title, boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
        this.title = title;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
