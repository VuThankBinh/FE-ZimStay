package com.datn.zimstay.api.models;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("userName")
    private String userName;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("address")
    private String address;

    @SerializedName("age")
    private int age;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("idCardNumber")
    private String idCardNumber;

    @SerializedName("confirmPassword")
    private String confirmPassword;

    public UpdateProfileRequest(String userName, String phoneNumber, String address, int age, 
                              String avatar, String idCardNumber, String confirmPassword) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.age = age;
        this.avatar = avatar;
        this.idCardNumber = idCardNumber;
        this.confirmPassword = confirmPassword;
    }

    // Getters and setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
} 