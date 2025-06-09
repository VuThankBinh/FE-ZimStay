package com.datn.zimstay.model;

import java.util.List;

public class Post {
    private int id;
    private String title;
    private String description;
    private int userId;
    private String createdAt;
    private String updatedAt;
    private boolean status;
    private List<Room> rooms;

    public Post() {
    }

    public Post(int id, String title, String description, int userId, String createdAt, String updatedAt, boolean status, List<Room> rooms) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.rooms = rooms;
    }

    // Getters and Setters
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

    // Inner class Room
    public static class Room {
        private int id;
        private int listing_id;
        private int apartment_id;

        public Room() {
        }

        public Room(int id, int listing_id, int apartment_id) {
            this.id = id;
            this.listing_id = listing_id;
            this.apartment_id = apartment_id;
        }

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