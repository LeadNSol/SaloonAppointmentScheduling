package com.leadnsolutions.saloonappointmentscheduling.notification.models;

public class StatusResponse {
    public String success;

    public StatusResponse() {
    }

    public StatusResponse(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }
}
