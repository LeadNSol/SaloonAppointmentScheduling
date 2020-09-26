package com.leadnsolutions.saloonappointmentscheduling.fragments.customer.model;

public class CustomerModel {

    String id, profile_image, name, email, password, phone, location, gender;

    public CustomerModel() {
    }

    public CustomerModel(String id, String profile_image, String name, String email,
                         String password, String phone, String location, String gender) {
        this.id = id;
        this.profile_image = profile_image;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.location = location;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return "CustomerModel{" +
                "id='" + id + '\'' +
                ", profile_image='" + profile_image + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
