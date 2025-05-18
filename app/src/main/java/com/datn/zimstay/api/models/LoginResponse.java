package com.datn.zimstay.api.models;

public class LoginResponse {
    private String status;
    private String message;
    private LoginData data;

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

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return "OK".equals(status);
    }

    public static class LoginData {
        private String token;
        private String type;
        private String email;
        private String avatar;
        private int typeOfAccount;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getTypeOfAccount() {
            return typeOfAccount;
        }

        public void setTypeOfAccount(int typeOfAccount) {
            this.typeOfAccount = typeOfAccount;
        }
    }
} 