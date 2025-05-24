package com.datn.zimstay.api.models;

import com.datn.zimstay.model.Apartment;

import java.util.List;

public class ApartmentsResponse {
    private String status;
    private String message;
    private List<Apartment> data;

    public ApartmentsResponse(String status, String message, List<Apartment> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

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

    public List<Apartment> getData() {
        return data;
    }

    public void setData(List<Apartment> data) {
        this.data = data;
    }
}
