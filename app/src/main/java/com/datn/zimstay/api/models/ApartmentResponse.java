package com.datn.zimstay.api.models;

public class ApartmentResponse {
    private String status;
    private String message;
    private ApartmentData data;

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public ApartmentData getData() { return data; }
    public void setData(ApartmentData data) { this.data = data; }
}

