package com.datn.zimstay.model;

import java.util.List;
import com.datn.zimstay.model.Image;
import com.datn.zimstay.model.Amenity;

public class Apartment {
    private int id;
    private String address;
    private String city;
    private String district;
    private String ward;
    private int cost;
    private String status;
    private int area;
    private int houseDeposit;
    private List<Image> images;
    private List<Amenity> amenities;

    // Getter, Setter (có thể dùng Alt+Insert trong Android Studio để generate)
    public List<Image> getImages() { return images; }
    public void setImages(List<Image> images) { this.images = images; }
    public List<Amenity> getAmenities() { return amenities; }
    public void setAmenities(List<Amenity> amenities) { this.amenities = amenities; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getHouseDeposit() {
        return houseDeposit;
    }

    public void setHouseDeposit(int houseDeposit) {
        this.houseDeposit = houseDeposit;
    }
}