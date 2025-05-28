package com.datn.zimstay.api.models;

public class checkAppointmentsResponse {
    private String status;
    private String message;
    private checkData data;

    public checkAppointmentsResponse(String status, String message, checkData data) {
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

    public checkData getData() {
        return data;
    }

    public void setData(checkData data) {
        this.data = data;
    }

    public static class checkData {
        private boolean hasPendingAppointment;

        public checkData(boolean hasPendingAppointments) {
            this.hasPendingAppointment = hasPendingAppointments;
        }

        public boolean isHasPendingAppointments() {
            return hasPendingAppointment;
        }

        public void setHasPendingAppointments(boolean hasPendingAppointments) {
            this.hasPendingAppointment = hasPendingAppointments;
        }
    }
}

