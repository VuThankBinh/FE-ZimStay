package com.datn.zimstay.api.models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ResetPasswordResponse {
    private String status;
    private String message;
    @SerializedName("data")
    private JsonObject data;

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

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }
}
