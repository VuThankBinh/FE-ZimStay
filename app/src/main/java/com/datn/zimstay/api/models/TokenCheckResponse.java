package com.datn.zimstay.api.models;

public class TokenCheckResponse {
    private String status;
    private String message;
    private TokenData data;

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

    public TokenData getData() {
        return data;
    }

    public void setData(TokenData data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return "OK".equals(status);
    }

    public static class TokenData {
        private String email;
        private int idUser;
        private String password;
        private int status;
        private int typeOfAccount;
        private String userName;
        private String phoneNumber;
        private String address;
        private int age;
        private String avatar;
        private boolean reviewapp;
        private int numOfViolations;
        private String idCardNumber;
        private String confirmPassword;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getIdUser() {
            return idUser;
        }

        public void setIdUser(int idUser) {
            this.idUser = idUser;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getTypeOfAccount() {
            return typeOfAccount;
        }

        public void setTypeOfAccount(int typeOfAccount) {
            this.typeOfAccount = typeOfAccount;
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

        public boolean isReviewapp() {
            return reviewapp;
        }

        public void setReviewapp(boolean reviewapp) {
            this.reviewapp = reviewapp;
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

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
} 