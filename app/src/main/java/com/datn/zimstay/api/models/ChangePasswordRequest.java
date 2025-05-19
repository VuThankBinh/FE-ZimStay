package com.datn.zimstay.api.models;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("email")
    private String email;
    @SerializedName("currentPassword")
    private String oldPassword;
    @SerializedName("newPassword")
    private String newPassword;
    @SerializedName("confirmNewPassword")
    private String confirmPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String email, String oldPassword, String newPassword, String confirmPassword) {
        this.email = email;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
