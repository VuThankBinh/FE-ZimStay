package com.datn.zimstay.api.models;

public class RegisterResponse {
    private String status;
    private String message;
    private RegisterData data;

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

    public RegisterData getData() {
        return data;
    }

    public void setData(RegisterData data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return "OK".equals(status);
    }

    public static class RegisterData {
        private int idUser;
        private String email;
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
        private int accountLevel;

        public int getIdUser() {
            return idUser;
        }

        public void setIdUser(int idUser) {
            this.idUser = idUser;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

        public int getAccountLevel() {
            return accountLevel;
        }

        public void setAccountLevel(int accountLevel) {
            this.accountLevel = accountLevel;
        }
    }
} 