package com.datn.zimstay.api.models;

import java.util.List;

public class ConversationResponse {
    private int id;
    private int user1Id;
    private int user2Id;
    private List<Integer> apartmentIds;
    private String createdAt;

    public ConversationResponse(int id, int user1Id, int user2Id, List<Integer> apartmentIds, String createdAt) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.apartmentIds = apartmentIds;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(int user1Id) {
        this.user1Id = user1Id;
    }

    public int getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(int user2Id) {
        this.user2Id = user2Id;
    }

    public List<Integer> getApartmentIds() {
        return apartmentIds;
    }

    public void setApartmentIds(List<Integer> apartmentIds) {
        this.apartmentIds = apartmentIds;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
