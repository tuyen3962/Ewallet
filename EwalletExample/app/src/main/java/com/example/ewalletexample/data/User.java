package com.example.ewalletexample.data;

import com.example.ewalletexample.Symbol.Symbol;
import com.example.ewalletexample.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String userId;
    private String fullName;
    private String password;
    private String phoneNumber;
    private String email;
    private String imgAccountLink;
    private String dateOfbirth;
    private String address;
    private String cmnd;

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User(UserModel model){
        setImgAccountLink(model.getImgLink());
        setFullName(model.getFullname());
    }

    public User(String fullName, String phoneNumber, String password, String email, String cmnd) {
        this.fullName = fullName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.cmnd = cmnd;
    }

    public User(){}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgAccountLink() {
        return imgAccountLink;
    }

    public void setImgAccountLink(String imgAccountLink) {
        this.imgAccountLink = imgAccountLink;
    }

    public String getDateOfbirth() {
        return dateOfbirth;
    }

    public void setDateOfbirth(String dateOfbirth) {
        this.dateOfbirth = dateOfbirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCmnd() {
        return cmnd;
    }

    public void setCmnd(String cmnd) {
        this.cmnd = cmnd;
    }
}