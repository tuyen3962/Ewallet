package com.example.ewalletexample.model;

public class PhoneModel {
    private String userID;
    private String phone;
    private String token;

    public PhoneModel(){

    }

    public PhoneModel(String userID, String phone, String token){
        this.userID = userID;
        this.phone = phone;
        this.token = token;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String phone) {
        this.userID = userID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
