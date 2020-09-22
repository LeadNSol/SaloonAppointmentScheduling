package com.leadnsolutions.saloonappointmentscheduling.notification.models;

public class Data {

    private String user, body, title, sent;
    private Integer icon;

    public Data() {
    }

    public Data(String user, String body, String title, String sent, Integer icon) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sent = sent;
        this.icon = icon;
    }

    public String getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public String getSent() {
        return sent;
    }

    public Integer getIcon() {
        return icon;
    }
}
