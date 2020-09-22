package com.leadnsolutions.saloonappointmentscheduling.saloon.model;

import java.util.List;

public class SaloonModel {
    String id, profile_image, name, email, password, phone, address, gender, loc, workTime, city;
    private List<SaloonService> saloonService;
    private List<SaloonRating> saloonRating;

    public SaloonModel() {
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getWorkTime() {
        return workTime;
    }

    public SaloonModel(String id, String profile_image, String name, String email,
                       String password, String phone, String location, String gender, String loc,
                       String workTime, String city, List<SaloonService> saloonService) {
        this.id = id;
        this.profile_image = profile_image;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = location;
        this.gender = gender;
        this.saloonService = saloonService;
        this.loc = loc;
        this.workTime = workTime;
        this.city = city;
    }

    public List<SaloonRating> getSaloonRating() {
        return saloonRating;
    }

    public String getCity() {
        return city;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setSaloonService(List<SaloonService> saloonService) {
        this.saloonService = saloonService;
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

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public List<SaloonModel.SaloonService> getSaloonService() {
        return saloonService;
    }

    public static class SaloonService {
        private String name, price;

        public SaloonService() {
        }

        public SaloonService(String name, String price) {
            this.name = name;
            this.price = price;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }
    }

    public static class SaloonRating {
        private String comment;
        private String rating;
        private String customerId;

        public SaloonRating() {
        }

        public SaloonRating(String comment, String rating, String customerId) {
            this.comment = comment;
            this.rating = rating;
            this.customerId = customerId;
        }

        public String getCustomerId() {
            return customerId;
        }

        public String getComment() {
            return comment;
        }

        public String getRating() {
            return rating;
        }
    }
}
