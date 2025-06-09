package com.datn.zimstay.model;

import java.util.List;

public class Listing {
    private int id;
    private String title;
    private String description;
    private int userId;
    private String createdAt;
    private boolean status;
    private List<Integer> apartmentIds;

    public Listing() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Integer> getApartmentIds() {
        return apartmentIds;
    }

    public void setApartmentIds(List<Integer> apartmentIds) {
        this.apartmentIds = apartmentIds;
    }
}