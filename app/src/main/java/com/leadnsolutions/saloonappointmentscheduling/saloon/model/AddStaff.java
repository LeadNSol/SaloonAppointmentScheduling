package com.leadnsolutions.saloonappointmentscheduling.saloon.model;

public class AddStaff {

    String sImage, sName, sPhone, sAddress, sDesignation, sFree;

    public AddStaff() {
    }

    public AddStaff(String sImage, String sName, String sPhone, String sAddress, String sDesignation
                    , String sFree) {
        this.sImage = sImage;
        this.sName = sName;
        this.sPhone = sPhone;
        this.sAddress = sAddress;
        this.sDesignation = sDesignation;
        this.sFree = sFree;
    }

    public String getsImage() {
        return sImage;
    }

    public String getsName() {
        return sName;
    }

    public String getsPhone() {
        return sPhone;
    }

    public String getsAddress() {
        return sAddress;
    }

    public String getsDesignation() {
        return sDesignation;
    }

    public String getsFree() {
        return sFree;
    }
}
