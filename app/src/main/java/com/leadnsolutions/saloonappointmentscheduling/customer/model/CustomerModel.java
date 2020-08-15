package com.leadnsolutions.saloonappointmentscheduling.customer.model;

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
}
