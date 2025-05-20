package com.datn.zimstay.api.models;

public class Amenity {
    private int id;
    private int amenityId;
    private String amenityName;
    private String amenityUnit;
    private int pricePerUnit;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAmenityId() { return amenityId; }
    public void setAmenityId(int amenityId) { this.amenityId = amenityId; }

    public String getAmenityName() { return amenityName; }
    public void setAmenityName(String amenityName) { this.amenityName = amenityName; }

    public String getAmenityUnit() { return amenityUnit; }
    public void setAmenityUnit(String amenityUnit) { this.amenityUnit = amenityUnit; }

    public int getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(int pricePerUnit) { this.pricePerUnit = pricePerUnit; }
}
