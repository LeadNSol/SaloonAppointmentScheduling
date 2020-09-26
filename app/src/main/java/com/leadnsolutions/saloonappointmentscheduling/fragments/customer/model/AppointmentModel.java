package com.leadnsolutions.saloonappointmentscheduling.fragments.customer.model;

public class AppointmentModel {
    private String uid,senderId, receiverId, time, day, timeStamp, status;

    public AppointmentModel() {
    }


    public AppointmentModel(String uid, String senderId, String receiverId,
                            String time, String day, String timeStamp, String status) {
        this.uid = uid;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.time = time;
        this.day = day;
        this.timeStamp = timeStamp;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

     public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
