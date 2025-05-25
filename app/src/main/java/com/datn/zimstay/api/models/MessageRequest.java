package com.datn.zimstay.api.models;

public class MessageRequest {
    private int conversationId;
    private int senderId;
    private int receiverId;
    private String message;

    public MessageRequest(int conversationId, int currentUserId, int otherUserId, String message) {
        this.conversationId = conversationId;
        this.senderId = currentUserId;
        this.receiverId = otherUserId;
        this.message = message;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getCurrentUserId() {
        return senderId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.senderId = currentUserId;
    }

    public int getOtherUserId() {
        return receiverId;
    }

    public void setOtherUserId(int otherUserId) {
        this.receiverId = otherUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
