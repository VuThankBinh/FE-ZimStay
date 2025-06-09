package com.datn.zimstay.api.models;

import java.util.List;

public class CreateListingResponse {
    private String status;
    private String message;
    private ListingData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ListingData getData() {
        return data;
    }

    public void setData(ListingData data) {
        this.data = data;
    }

    public static class ListingData {
        private int id;
        private String title;
        private String description;
        private int userId;
        private String createdAt;
        private String updatedAt;
        private boolean status;
        private List<Room> rooms;

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

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public List<Room> getRooms() {
            return rooms;
        }

        public void setRooms(List<Room> rooms) {
            this.rooms = rooms;
        }
    }

    public static class Room {
        private int id;
        private int listing_id;
        private int apartment_id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getListing_id() {
            return listing_id;
        }

        public void setListing_id(int listing_id) {
            this.listing_id = listing_id;
        }

        public int getApartment_id() {
            return apartment_id;
        }

        public void setApartment_id(int apartment_id) {
            this.apartment_id = apartment_id;
        }
    }
} 