package com.datn.zimstay.api.models;

import com.datn.zimstay.model.Apartment;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private Apartment data;

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

    public Apartment getData() {
        return data;
    }

    public void setData(Apartment data) {
        this.data = data;
    }
}
