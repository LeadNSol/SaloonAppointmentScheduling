package com.leadnsolutions.saloonappointmentscheduling.notification.models;

public class Sender {


    private Data data;
    private String to;

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    public Sender() {
    }

    public Data getData() {
        return data;
    }

    public String getTo() {
        return to;
    }
}
