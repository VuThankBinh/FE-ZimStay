package com.datn.zimstay.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MessageResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("conversationId")
    private int conversationId;

    @SerializedName("senderId")
    private int senderId;

    @SerializedName("senderName")
    private String senderName;

    @SerializedName("senderAvatar")
    private String senderAvatar;

    @SerializedName("message")
    private String message;

    @SerializedName("createdAt")
    private String createdAt;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
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

    public MessageResponse(int id, int conversationId, int senderId, String senderName, String senderAvatar, String message, String createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.message = message;
        this.createdAt = createdAt;
    }
}