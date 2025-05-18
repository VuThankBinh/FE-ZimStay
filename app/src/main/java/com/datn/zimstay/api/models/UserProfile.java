package com.datn.zimstay.api.models;

import com.google.gson.annotations.SerializedName;

public class UserProfile {
    @SerializedName("email")
    private String email;

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

    @SerializedName("numOfViolations")
    private int numOfViolations;

    @SerializedName("idCardNumber")
    private String idCardNumber;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public int getNumOfViolations() {
        return numOfViolations;
    }

    public void setNumOfViolations(int numOfViolations) {
        this.numOfViolations = numOfViolations;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }
} 