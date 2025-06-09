package com.datn.zimstay.api.models;

public class ListingCountResponse {
    private String status;
    private String message;
    private ListingCountData data;

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

    public ListingCountData getData() {
        return data;
    }

    public void setData(ListingCountData data) {
        this.data = data;
    }

    public class ListingCountData{
        private int maxAllowed;
        private int userLevel;
        private int currentCount;
        private int remaining;

        public int getMaxAllowed() {
            return maxAllowed;
        }

        public void setMaxAllowed(int maxAllowed) {
            this.maxAllowed = maxAllowed;
        }

        public int getUserLevel() {
            return userLevel;
        }

        public void setUserLevel(int userLevel) {
            this.userLevel = userLevel;
        }

        public int getCurrentCount() {
            return currentCount;
        }

        public void setCurrentCount(int currentCount) {
            this.currentCount = currentCount;
        }

        public int getRemaining() {
            return remaining;
        }

        public void setRemaining(int remaining) {
            this.remaining = remaining;
        }
    }
} 