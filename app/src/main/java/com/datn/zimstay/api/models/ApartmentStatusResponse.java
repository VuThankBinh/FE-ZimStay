package com.datn.zimstay.api.models;

import com.google.gson.annotations.SerializedName;

public class ApartmentStatusResponse {
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private ApartmentData data;

    public static class ApartmentData {
        @SerializedName("id")
        private int id;
        
        @SerializedName("ownerId")
        private int ownerId;
        
        @SerializedName("address")
        private String address;
        
        @SerializedName("city")
        private String city;
        
        @SerializedName("district")
        private String district;
        
        @SerializedName("ward")
        private String ward;
        
        @SerializedName("detaildc")
        private String detaildc;
        
        @SerializedName("cost")
        private double cost;
        
        @SerializedName("status")
        private String status;
        
        @SerializedName("area")
        private double area;
        
        @SerializedName("houseDeposit")
        private double houseDeposit;
        
        @SerializedName("createdAt")
        private String createdAt;
        
        @SerializedName("location")
        private String location;
        
        @SerializedName("images")
        private Object[] images;
        
        @SerializedName("amenities")
        private Object[] amenities;

        // Getters
        public int getId() { return id; }
        public int getOwnerId() { return ownerId; }
        public String getAddress() { return address; }
        public String getCity() { return city; }
        public String getDistrict() { return district; }
        public String getWard() { return ward; }
        public String getDetaildc() { return detaildc; }
        public double getCost() { return cost; }
        public String getStatus() { return status; }
        public double getArea() { return area; }
        public double getHouseDeposit() { return houseDeposit; }
        public String getCreatedAt() { return createdAt; }
        public String getLocation() { return location; }
        public Object[] getImages() { return images; }
        public Object[] getAmenities() { return amenities; }
    }

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public ApartmentData getData() { return data; }
} 